#!/usr/bin/env python
from ants import *
from render import *
from ai_state import *
from hill_scan import *
import traceback

# A class that stores most of the game infomation
class game_state():
    # Setup the class
	def __init__(self, ants, logger):
		try:		
			# Setup shared variables
			self.ants = ants
			self.logger = logger
			self.render = render(ants, self)
			self.turn = 0
			self.diffuse_rate = DIFFUSE_RATE
			self.coefficient = COEFFICIENT
			
			# Time out setup
			total_time = self.ants.turntime
			safe_time = total_time * TIME_OUT_BUFFER
			self.time_out_vision = total_time - (TIME_OUT_VISION * safe_time)
			self.time_out_diffuse = self.time_out_vision - (TIME_OUT_DIFFUSE * safe_time)
			self.logger.debug("Time out total: %s , safe: %s", total_time, safe_time)
			self.logger.debug("Time out vision: %s", self.time_out_vision)
			self.logger.debug("Time out diffuse: %s", self.time_out_diffuse)
			
			self.kill_time = self.ants.turns * KILL_TIME
			self.logger.debug("Kill time: %s", self.kill_time)
			
			# Setup my ant variables
			self.my_hills = set()
			self.my_hill_scans = set()
			self.num_my_hills = 0
			self.prev_my_hills = 0
			
			self.my_dead_ants = []
			self.my_ants = set()
			self.movable = set()
			self.num_my_ants = 0		
			
			# Setup enemy ant variables
			self.owner_set = set()
			self.owner_count = 0
			self.enemy_dead_ants = []
			self.enemy_ants = [set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set()]
			self.enemy_hills = [set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set(), set()]
			self.enemy_hill_scans = set()
			self.num_enemy_ants = 0

			# Setup food variables
			self.food = set()
			
			self.desired = set()
			self.weakest = 0
			self.target = 0
			self.confidence = 0
			self.attack_ant = None
			self.attack_target = None
			self.food_target = None
			self.path_max = PATH_MAX
			self.paths = []

			# Other maps
			self.occupied = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.mobile_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.food_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.seen = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.passable = [[True]*self.ants.cols for row in range(self.ants.rows)]
			self.vision = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.diffusion = [[0]*self.ants.cols for row in range(self.ants.rows)]
			self.new_diffusion = [[0]*self.ants.cols for row in range(self.ants.rows)]
			self.my_attack_map = [[0]*self.ants.cols for row in range(self.ants.rows)]
			self.enemy_attack_map = [[0]*self.ants.cols for row in range(self.ants.rows)]
			self.nodes = []
						
			# Setup vision offset
			self.vision_offsets_2 = []
			mx = int(sqrt(self.ants.viewradius2))
			for d_row in range(-mx,mx+1):
				for d_col in range(-mx,mx+1):
					d = d_row**2 + d_col**2
					if d <= self.ants.viewradius2:
						self.vision_offsets_2.append((
							d_row%self.ants.rows-self.ants.rows,
							d_col%self.ants.cols-self.ants.cols
						))
			
			# Setup attack offset
			radius = self.ants.attackradius2 * ATTACK_BUFFER
			self.enemy_attack_offsets_2 = []
			mx = int(sqrt(radius))
			for d_row in range(-mx,mx+1):
				for d_col in range(-mx,mx+1):
					d = d_row**2 + d_col**2
					if d <= radius:
						self.enemy_attack_offsets_2.append((
							d_row%self.ants.rows-self.ants.rows,
							d_col%self.ants.cols-self.ants.cols
						))
						
			# Setup attack offset
			radius = self.ants.attackradius2 * DEFEND_BUFFER
			self.my_attack_offsets_2 = []
			mx = int(sqrt(radius))
			for d_row in range(-mx,mx+1):
				for d_col in range(-mx,mx+1):
					d = d_row**2 + d_col**2
					if d <= radius:
						self.my_attack_offsets_2.append((
							d_row%self.ants.rows-self.ants.rows,
							d_col%self.ants.cols-self.ants.cols
						))
			
			# Full sized maps
			self.west_map = []
			self.east_map = []
			self.north_map = []
			self.south_map = []
					
			for row in range(self.ants.rows):
				self.west_map.append((row - 1) % self.ants.rows)
				self.east_map.append((row + 1) % self.ants.rows)
				
			for col in range(self.ants.cols):
				self.north_map.append((col - 1) % self.ants.cols)
				self.south_map.append((col + 1) % self.ants.cols)
			
		except:
			self.logger.error("Error game_state init: print_exc(): %s", traceback.format_exc())
	
	# Add everything to the diffusion
	def update_diffusion(self):	
		try:
			self.nodes = []
			self.min_nodes = []
		
			# Boost value of fog and explore area
			for row in range(self.ants.rows):
				for col in range(self.ants.cols):
					if not self.vision[row][col] and self.seen[row][col]:
						loc = (row, col)
						self.min_nodes.append( (loc, FOG) )	
					elif not self.vision[row][col] and not self.seen[row][col]:
						loc = (row, col)
						self.min_nodes.append( (loc, EXPLORE) )	

			## Add enemies dead to the diffusion map
			#for loc in self.enemy_dead_ants:
			#	self.nodes.append( (loc, ENEMY_DEAD_ANT) )		
			#
			## Add my dead ants to the diffusion map
			#for loc in self.my_dead_ants:
			#	self.nodes.append( (loc, MY_DEAD_ANT) )						
		
			# Add food to the diffusion map
			for loc in self.food:
				self.nodes.append( (loc, FOOD_VALUE) )
			
			# Add enemies to the diffusion mapS
			for index in range(1,7):
				for loc in self.enemy_ants[index]:
					row, col = loc
					self.nodes.append( (loc, ENEMY_ANT) )
					
			# Add enemy hills to the diffusion map
			for loc in self.enemy_hills[0]:
				self.nodes.append( (loc, ENEMY_HILL) )
				
			# Add my hills to the diffusion map
			for loc in self.my_hills:
				my_dist = self.closest_my_ant(loc)
				enemy_dist = self.closest_enemy_ant(loc)
				if enemy_dist < my_dist + ENEMY_BUFFER:
					self.nodes.append( (loc, MY_DANGER_HILL) )
				else:
					self.nodes.append( (loc, MY_SAFE_HILL) )

			# Add my ants to the diffusion map
			for loc in self.my_ants:
				row, col = loc
				if self.enemy_attack_map[row][col] > 0:#self.my_attack_map[row][col]:
					self.nodes.append( (loc, MY_DANGER_ANT) )
				else:
					self.nodes.append( (loc, MY_SAFE_ANT) )
		except:
			self.logger.error("Error update diffusion: print_exc(): %s", traceback.format_exc())
			
	# Diffuse the map so scents get diffused
	def apply_diffuse(self):
		try:
			for loc, value in self.nodes:
				row, col = loc
				self.diffusion[row][col] = value			
			
			for loc, value in self.min_nodes:
				row, col = loc
				if self.diffusion[row][col] < value:
					self.diffusion[row][col] = value

		except:
			self.logger.error("Error apply diffusion: print_exc(): %s", traceback.format_exc())
	
	# Diffuse the map so scents get disbrust
	def diffuse(self, amount, time_out):
		try:
			copied = False
			count = 0
			self.apply_diffuse()
			for index in range(amount):
				# Check how much time we have left
				if time_out > self.ants.time_remaining():
					self.logger.debug("TIMEOUT Diffusing ran of time %s", self.ants.time_remaining())
					# reduce diffuse rate a little
					self.diffuse_rate -= 1
					if self.diffuse_rate < 3:
						self.diffuse_rate = 3
					if not copied:
						self.diffuse_copy()
					return

				copied = True
				self.diffuse_copy()
				self.apply_diffuse()			
			
				for row in range(self.ants.rows):
					for col in range(self.ants.cols):			
						if self.passable[row][col]:
							# Use the map array to find neighbours			
							min_row = self.west_map[row]
							max_row = self.east_map[row]
							min_col = self.north_map[col]
							max_col = self.south_map[col]	

							# Unroll diffusion total
							self.new_diffusion[row][col] = \
								( self.diffusion[row][min_col] \
								+ self.diffusion[min_row][col] \
								+ self.diffusion[row][col] \
								+ self.diffusion[max_row][col] \
								+ self.diffusion[row][max_col] ) \
								* self.coefficient
						else:
							self.new_diffusion[row][col] = BLOCKED					

			self.diffuse_copy()					
		except:
			self.logger.error("Error diffusion: print_exc(): %s", traceback.format_exc())

	# Copy the new diffusion map to the old one
	def diffuse_copy(self):
		#self.logger.debug("Copying the full diffusion map")
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):				
				self.diffusion[row][col] = self.new_diffusion[row][col]
					
				
	# Update the occupy ants map
	def occupy_ants(self):	
		try:
			for loc in self.my_ants:
				row, col = loc
				self.occupied[row][col] = True
				self.mobile_map[row][col] = True
				
			#for loc in self.my_hills:
			#	row, col = loc
			#	self.occupied[row][col] = True			
				
			for loc in self.food:
				row, col = loc
				self.food_map[row][col] = True
		except:
			self.logger.error("Error occupy: print_exc(): %s", traceback.format_exc())
			
	#return the value of my closest ant to passed hill
	def closest_my_ant(self, hill_loc):
		test_scan = None
		closest = 999999
		
		# Find the test scan to check
		for scan in self.my_hill_scans:
			if scan.loc == hill_loc:
				test_scan = scan
				break
		
		#Find the closest ant from the test scan
		if test_scan != None:
			for ant_loc in self.my_ants:
				row, col = ant_loc
				dist = test_scan.g_map[row][col]
				if dist < closest and dist != -1:
					closest = dist
					
		return closest
		
	#return the value of my closest ant to passed hill
	def closest_enemy_ant(self, hill_loc):
		test_scan = None
		closest = 999999
		
		# Find the test scan to check
		for scan in self.my_hill_scans:
			if scan.loc == hill_loc:
				test_scan = scan
				break
		
		#Find the closest ant from the test scan
		if test_scan != None:
			for enemy_set in self.enemy_ants:
				for ant_loc in enemy_set:
					row, col = ant_loc
					dist = test_scan.g_map[row][col]
					if dist < closest and dist != -1:
						closest = dist
					
		return closest
		
	# Figure out the my attack map
	def my_attack(self):
		try:
			# loop through ants and set all squares around ant as visible
			for ant_loc in self.my_ants:
				a_row, a_col = ant_loc
				for v_row, v_col in self.my_attack_offsets_2:
					self.my_attack_map[a_row+v_row][a_col+v_col] += 1			
		except:
			self.logger.error("Error my_attack: print_exc(): %s", traceback.format_exc())
			
	# Figure out the my attack map
	def move_attack(self, old_loc, new_loc):
		try:
			# Remove old influence
			o_row, o_col = old_loc
			for v_row, v_col in self.my_attack_offsets_2:
				self.my_attack_map[o_row+v_row][o_col+v_col] -= 1

			# Add new influence
			n_row, n_col = new_loc
			for v_row, v_col in self.my_attack_offsets_2:
				self.my_attack_map[n_row+v_row][n_col+v_col] += 1					
		except:
			self.logger.error("Error move_attack: print_exc(): %s", traceback.format_exc())
			
	# Figure out the enemy attack map
	def enemy_attack(self):
		try:
			# loop through ants and set all squares around ant as visible
			for ant_loc in self.enemy_ants[0]:
				a_row, a_col = ant_loc
				for v_row, v_col in self.enemy_attack_offsets_2:
					self.enemy_attack_map[a_row+v_row][a_col+v_col] += 1			
		except:
			self.logger.error("Error enemy_attack: print_exc(): %s", traceback.format_exc())

	# Figure out the vision map
	def my_vision(self, time_out):
		try:
			# loop through ants and set all squares around ant as visible
			for ant_loc in self.my_ants:
				a_row, a_col = ant_loc
				for v_row, v_col in self.vision_offsets_2:
					self.vision[a_row+v_row][a_col+v_col] = True
			
			for row in range(self.ants.rows):
				for col in range(self.ants.cols):	
					if self.vision[row][col]:
						loc = (row, col)
						if not self.seen[row][col]:
							self.seen[row][col] = True
							self.passable[row][col] = self.ants.passable(loc)

		except:
			self.logger.error("Error vision: print_exc(): %s", traceback.format_exc())
	
	# Update enemy hills
	def update_enemy_hills(self):
		try:
			new_enemy_hills = self.ants.enemy_hills()
			self.num_enemy_hills = len(new_enemy_hills)
			hill_set = set()			
			
			# Add new hill scans
			for hill_loc, owner in new_enemy_hills:
				hill_set.add(hill_loc)
				if hill_loc not in self.enemy_hills[owner]:
					self.enemy_hill_scans.add(hill_scan(self, hill_loc, owner))
					self.enemy_hills[owner].add(hill_loc)
					self.enemy_hills[0].add(hill_loc)
					
			# Remove razed hills
			for index in range(1, len(self.enemy_hills)):				
				for hill_loc in self.enemy_hills[index].copy():
					row, col = hill_loc
					if self.vision[row][col] and hill_loc not in hill_set:
						for scan in self.enemy_hill_scans.copy():
							if scan.loc == hill_loc:
								self.enemy_hill_scans.remove(scan)
								self.enemy_hills[index].remove(hill_loc)
								self.enemy_hills[0].remove(hill_loc)
		except:
			self.logger.error("Error update enemy hills: print_exc(): %s", traceback.format_exc())

	# Update enemy ant info	
	def update_enemy_ants(self):
		try:
			new_enemy_ants = self.ants.enemy_ants()
			
			self.num_enemy_ants = 0			
			# Clear enemy ants
			for ant_set in self.enemy_ants:
				ant_set.clear()		
			
			# Record the new ants		
			for loc, owner in new_enemy_ants:
				self.num_enemy_ants += 1
				self.enemy_ants[owner].add(loc)
				self.enemy_ants[0].add(loc)
				self.owner_set.add(owner)
				
			self.owner_count = len(self.owner_set)
		except:
			self.logger.error("Error update enemy ants: print_exc(): %s", traceback.format_exc())

	# Update the food info	
	def update_food(self):
		try:
			new_food = set(self.ants.food())
			
			# If the food is visible test if it still exist
			for food_loc in self.food.copy():			
				row, col = food_loc
				if self.vision[row][col]:
				
					# Look for the food in the updated list
					found = False
					for loc in new_food:
						if loc == food_loc:
							found = True
							break
							
					# If the food doesn't exist then the food has been razed
					if not found:
						self.food.remove(food_loc)
						
			for loc in new_food:
				self.food.add(loc)
		except:
			self.logger.error("Error update food: print_exc(): %s", traceback.format_exc())

	# Update my ants info	
	def update_my_ants(self):
		try:
			self.my_ants = set(self.ants.my_ants())
			self.movable = set(self.my_ants)
			self.num_my_ants = len(self.my_ants)
		except:
			self.logger.error("Error update my ants: print_exc(): %s", traceback.format_exc())

	# Update my hills info	
	def update_my_hills(self):				
		try:
			new_hills = set(self.ants.my_hills())
			self.num_my_hills = len(new_hills)
			
			# Add new hill scans
			for hill_loc in new_hills:
				if hill_loc not in self.my_hills:
					self.my_hill_scans.add(hill_scan(self, hill_loc, 0))
			
			# Remove razed hills
			for hill_loc in self.my_hills:
				if hill_loc not in new_hills:
					for scan in self.my_hill_scans.copy():
						if scan.loc == hill_loc:
							self.my_hill_scans.remove(scan)
							
			self.my_hills = new_hills	
		except:
			self.logger.error("Error update my ants: print_exc(): %s", traceback.format_exc())
			
	# Update enemy ant info	
	def update_dead_ants(self):
		try:
			dead_ants = self.ants.dead_list.items()
			self.logger.debug("dead_ants %s", dead_ants)
	
			# Clear 1 dead ant
			#self.my_dead_ants.pop(0)
			#self.enemy_dead_ants.pop(0)
			self.my_dead_ants = []
			self.enemy_dead_ants = []				
			
			# Record the new  dead ants		
			for loc, owner in dead_ants:
				if owner == 0:
					self.my_dead_ants.append(loc)
				else:
					self.enemy_dead_ants.append(loc)
		except:
			self.logger.error("Error update enemy ants: print_exc(): %s", traceback.format_exc())
			
	# Update the path info	
	def update_paths(self):
		try:
			#Step through any existing paths
			for current in self.paths:
				for loc in current.path:
					row, col = loc
					b_loc = self.find_bucket[row][col]
					b_row, b_col = b_loc
					self.bucket_map[b_row][b_col].g_path += 1
		except:
			self.logger.error("Error update paths: print_exc(): %s", traceback.format_exc())
	
	# Do the start up collection of data
	def start_turn(self):
		try:
			# Increment turn number
			self.turn += 1
			
			self.owner_set = set()			
			self.occupied = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.mobile_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.food_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.vision = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.my_attack_map = [[0]*self.ants.cols for row in range(self.ants.rows)]
			self.enemy_attack_map = [[0]*self.ants.cols for row in range(self.ants.rows)]

			self.logger.debug("Collecting my ants time remaining %d", self.ants.time_remaining())
			self.update_my_hills()
			self.update_my_ants()
			
			self.logger.debug("Calculate vision time remaining %d", self.ants.time_remaining())
			self.my_vision(self.time_out_vision)
			
			self.logger.debug("Collecting food and enemy time remaining %d", self.ants.time_remaining())
			self.update_food()
			self.update_enemy_ants()
			self.update_enemy_hills()
			self.update_paths()
			self.update_dead_ants()
			
			self.logger.debug("Combat time remaining %d", self.ants.time_remaining())
			self.my_attack()
			self.enemy_attack()
			
			self.logger.debug("Scanning hills %d", self.ants.time_remaining())
			for scan in self.my_hill_scans:
				scan.scan(SCAN_LIMIT)
				
			for scan in self.enemy_hill_scans:
				scan.scan(SCAN_LIMIT)
					
		except:
			self.logger.error("Error start turn: print_exc(): %s", traceback.format_exc())
			
	def render_stats(self):
		try:
			#self.logger.debug("ant food %s", self.food)
			#self.logger.debug("my ants (%d): %s", self.num_my_ants, self.my_ants)
			#self.logger.debug("enemy ants (%d): %s", self.num_enemy_ants, self.enemy_ants)
			#self.logger.debug("my hills: %s", self.my_hills)
			#self.logger.debug("enemy hills: %s", self.enemy_hills)
			#self.logger.debug("Owner set %s count %s", self.owner_set, self.owner_count)
			#self.logger.debug("engine enemy hills: %s", self.ants.enemy_hills())
			#self.logger.debug("enemy dead: %s", self.enemy_dead_ants)
			#self.logger.debug("my dead: %s", self.my_dead_ants)
			#self.logger.debug("map:\n%s", self.ants.render_text_map())
			#self.logger.debug("seen:\n%s", self.render.render_seen())
			#self.logger.debug("vision:\n%s", self.render.render_vision())
			#self.logger.debug("occupy:\n%s", self.render.render_occupy())
			#self.logger.debug("passable:\n%s", self.render.render_passable())
			#self.logger.debug("diffusion:\n%s", self.render.render_diffusion())
			#self.logger.debug("Paths:\n%s", self.render.render_paths())+
			#self.logger.debug("G score map:\n%s", self.render.render_g_map())
			#self.logger.debug("my attack:\n%s", self.render.render_my_attack())
			#self.logger.debug("enemy attack:\n%s", self.render.render_enemy_attack())
			#self.logger.debug("hill scan:\n%s", self.render.render_enemy_hill_scan())
			pass
		except:
			self.logger.error("Error start turn: print_exc(): %s", traceback.format_exc())