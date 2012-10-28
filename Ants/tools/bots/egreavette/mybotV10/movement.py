#!/usr/bin/env python
import sys
from ants import *
from ai_state import *
from game_state import *
from pathfinder import *
from path import *
from hill_scan import *
import traceback



# This class control most of the nitty gritty movement stuff
class movement():
	# Setup the class
	def __init__(self, ants, logger, game_state):
		self.ants = ants
		self.logger = logger
		self.game_state = game_state
		self.dir_rotation = [('n','e','s','w','m'),
							 ('e','s','w','m','n'),
							 ('s','w','m','n','e'),
							 ('w','m','n','e','s'),
							 ('m','n','e','s','w')]
							 
		self.aim = {'n': (-1, 0),
					'e': (0, 1),
					's': (1, 0),
					'w': (0, -1),
					'm': (0, 0)}							 
		
		self.dir_current = 0
		self.pathfinder = pathfinder(ants, logger, game_state)
		
	# Move all the ants in the correct order
	def move_all(self, path):
		try:
			length = len(path) - 2
			
			for index in range(length, -1, -1):		
				# Get the direction of the next step
				direction = self.ants.direction(path[index], path[index + 1])[0]

				# Try to continue on the next step in the path
				if not self.go_direction(path[index], direction):
					#self.logger.debug("blocked %s at index %d", path, index)
					return
		except:
			self.logger.error("Error move all: print_exc(): %s", traceback.format_exc())

	# Test if the next move is legal and add to order list if true
	def go_direction(self, loc, direction):
		try:
			row, col = loc
			# Figure out the new location next bots next move
			new_loc = self.ants.destination(loc, direction)
			n_row, n_col = new_loc
				
			# Check if the new location is passable and unoccupied
			passable = self.game_state.passable[n_row][n_col]
			unoccupied = not self.game_state.occupied[n_row][n_col]
			food = not self.game_state.food_map[n_row][n_col]			
			
			# If the new location is unoccupied then issue order and return true
			if (unoccupied and passable and food):
				#self.logger.debug("Move issued %s %s %s", row, col, direction)
				sys.stdout.write('o %s %s %s\n' % (row, col, direction))
				sys.stdout.flush()
				
				# Update the maps
				self.game_state.mobile_map[row][col] = False
				self.game_state.occupied[n_row][n_col] = True

				if loc not in self.game_state.my_hills:
					self.game_state.occupied[row][col] = False				
				
				# Update the game state of the new ants location
				self.game_state.my_ants.discard(loc)
				self.game_state.movable.discard(loc)
				self.game_state.my_ants.add(new_loc)
			
				return True
			# Else don't do anything but return false
			else:
				return False
		except:
			self.logger.error("Error go_direction: print_exc(): %s", traceback.format_exc())

	# Similar to the ants version except it accepts 'm' for middle
	def destination(self, loc, direction):
		row, col = loc
		d_row, d_col = self.aim[direction]
		return ((row + d_row) % self.ants.rows, (col + d_col) % self.ants.cols)
	
	# Return the scan of the closest hill for passed location
	def closest_safe_hill(self, loc):
		row, col = loc
		lowest = 999999
		lowest_scan = None
		
		for scan in self.game_state.my_hill_scans:
				if scan.g_map[row][col] < lowest and scan.g_map[row][col] != -1:
					lowest = scan.g_map[row][col]
					lowest_scan = scan
					
		return lowest_scan
		
	# Return the scan of the closest hill for passed owner
	def closest_enemy_hill(self, owner):
		overall = 999999
		overall_scan = None
		
		for my_hill in self.game_state.my_hills:
			row, col = my_hill
			lowest = 999999
			lowest_scan = None			
			
			for scan in self.game_state.enemy_hill_scans:
				if scan.owner == owner and scan.g_map[row][col] < lowest and scan.g_map[row][col] != -1:
					lowest = scan.g_map[row][col]
					lowest_scan = scan
					
			if lowest < overall:
				overall = lowest
				overall_scan = lowest_scan				
					
		return overall_scan
		
	# Get how confident the computer is about attacking
	def get_confidence(self):
		try:
			if self.game_state.num_enemy_ants == 0:
				return 0
			else:
				return self.game_state.num_my_ants / self.game_state.num_enemy_ants
		except:
			self.logger.error("Error get_confidence: print_exc(): %s", traceback.format_exc())
		
	# Look for  the weakest team
	def weakest_enemy(self):
		try:
			lowest = 9999999
			weakest = 0
			for index in range(1,7):
				num_ants = len(self.game_state.enemy_ants[index])
				if num_ants < lowest and num_ants != 0:
					lowest = num_ants
					weakest = index

			return weakest
		except:
			self.logger.error("Error weakest_enemy: print_exc(): %s", traceback.format_exc())
	
			
	# Test if the next move is legal and add to order list if true
	# This function has been inlined to speed it up.
	def climb_hills(self):
		try:
			confidence = self.get_confidence()
			weakest = self.weakest_enemy()
			attack_scan = None
			
			self.logger.debug("Confidence %s and weakest %s", confidence, weakest)
			
			# If confidence level is high then attack
			if confidence > CONFIDENCE_LEVEL:
				self.logger.debug("Ants using attack map")
				attack_scan = self.closest_enemy_hill(weakest)
		
			for loc in self.game_state.movable.copy():
				row, col = loc
				
				retreat_scan = None				
				
				if self.game_state.enemy_attack_map[row][col] > self.game_state.my_attack_map[row][col]:
					retreat_scan = self.closest_safe_hill(loc)
					
				highest = -999999				
				high_direction = 'm'
				high_loc = loc
				
				directions = self.dir_rotation[self.dir_current]
				for direction in directions:
					# Figure out the new location next bots next move
					new_loc = self.destination(loc, direction)
					n_row, n_col = new_loc
					
					# Check if the new location is passable and unoccupied
					passable = self.game_state.passable[n_row][n_col]
					unoccupied = not self.game_state.occupied[n_row][n_col]
					food = not self.game_state.food_map[n_row][n_col]
					
					# Figure out my attack potential vs enemy potential
					my_attack = self.game_state.my_attack_map[n_row][n_col]
					enemy_attack = self.game_state.enemy_attack_map[n_row][n_col]
					if self.game_state.turn > self.game_state.kill_time:						
						attack = enemy_attack <= my_attack
					else:
						attack = enemy_attack < my_attack
						
					# If in retreat mode then apply the retreat map
					if retreat_scan != None:
						if retreat_scan.g_map[n_row][n_col] != -1:
							new_value = retreat_scan.g_highest - retreat_scan.g_map[n_row][n_col]
						else:
							new_value = -1
					
					# If in attack mode apply the attack map
					elif attack_scan != None:
						if self.game_state.owner_count == 1:
							max = 9999999
						else:
							max = ATTACK_LIMIT
							
						if attack_scan.g_map[n_row][n_col] != -1 and attack_scan.g_map[n_row][n_col] < ATTACK_LIMIT:
							new_value = attack_scan.g_highest - attack_scan.g_map[n_row][n_col] + self.game_state.diffusion[n_row][n_col]
						else:
							new_value = self.game_state.diffusion[n_row][n_col]
					else:
						new_value = self.game_state.diffusion[n_row][n_col]
					
					# If the new location is unoccupied then issue order and return true
					if attack and unoccupied and passable and food and highest < new_value:
						highest = new_value
						high_loc = new_loc
						high_direction = direction
							
				if high_direction != 'm':			
					#self.logger.debug("Move issued %s %s %s", row, col, high_direction)
					sys.stdout.write('o %s %s %s\n' % (row, col, high_direction))
					sys.stdout.flush()
					
					h_row, h_col = high_loc
					
					# Update attack map
					self.game_state.move_attack(loc, high_loc)
					
					# Update the maps
					self.game_state.mobile_map[row][col] = False
					self.game_state.occupied[row][col] = False
					self.game_state.occupied[h_row][h_col] = True				
					
					# Update the game state of the new ants location
					self.game_state.movable.discard(loc)
					self.game_state.my_ants.discard(loc)
					self.game_state.my_ants.add(high_loc)
					
			#Wrap arround directions
			self.dir_current += 1
			if self.dir_current >= 5:
				self.dir_current = 0
		except:
			self.logger.error("Error climb_hills: print_exc(): %s", traceback.format_exc())
			
	# Move all the ants in the correct order
	def clear_hills(self):
		try:
			for hill_loc in self.game_state.my_hills:
				if hill_loc in self.game_state.movable:
					path = self.pathfinder.hill_push(hill_loc, HILL_LIMIT)
					if path:
						self.move_all(path)
		except:
			self.logger.error("Error clear_hills: print_exc(): %s", traceback.format_exc())
	
	# Setup an attack path to go on
	def hunt(self):
		try:
			path_count = len(self.game_state.paths)
		
			# Check if it's time to look for a new attack path
			if path_count <= self.game_state.path_max:			
				if self.game_state.attack_ant != None and self.game_state.attack_target != None:
					#self.logger.debug("looking for new attack path to %s", self.game_state.attack_target)
					attack_path = self.pathfinder.a_star(self.game_state.attack_ant, self.game_state.attack_target, STAR_LIMIT)
										
					#self.logger.debug("Path found %s", attack_path)
					if attack_path:
						self.game_state.paths.append(path(attack_path, ATTACK_PATH_STEP))
		except:
			self.logger.error("Error hunt: print_exc(): %s", traceback.format_exc())
			
	# Setup an attack path to go on
	def eat(self):
		try:
			path_count = len(self.game_state.paths)
		
			# Check if it's time to look for a new attack path
			if path_count <= self.game_state.path_max:	
				if self.game_state.attack_ant != None and self.game_state.food_target != None:
					#self.logger.debug("looking for new food path to %s", self.game_state.food_target)
					food_path = self.pathfinder.a_star(self.game_state.attack_ant, self.game_state.food_target, STAR_LIMIT)
										
					#self.logger.debug("Path found %s", food_path)
					if food_path:
						self.game_state.paths.append(path(food_path, FOOD_PATH_STEP))
		except:
			self.logger.error("Error food: print_exc(): %s", traceback.format_exc())


	# Setup an attack path to go on
	def step_paths(self):
		try:		
			#Step through any existing paths
			for current in self.game_state.paths[:]:
				
				# Check if we should end the path
				if current.count > current.max_count:
					#self.logger.debug("Remove path %s", current.path )
					self.game_state.paths.remove(current)
					continue
					
				step_count = 0
				score = PATH_BEGIN
				attack_nodes = range(current.cycle_step, current.length, current.step)
				for loc in current.path:
					if step_count in attack_nodes:
						self.game_state.nodes.append( (loc, score) )
						#self.logger.debug("attack_nodes for adding loc %s score %s",loc,score )
						
					score += current.score_step
					step_count += 1
						
				current.count += 1
				current.cycle_step += 1
		except:
			self.logger.error("Error step paths: print_exc(): %s", traceback.format_exc())

