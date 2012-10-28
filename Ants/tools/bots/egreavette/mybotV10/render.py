#!/usr/bin/env python
# A class that stores most of the game infomation
class render():
    # Setup the class
	def __init__(self, ants, game_state):
		# Setup share variables
		self.ants = ants
		self.game_state = game_state
		
	# Render a text map of the food/hill diffusion map
	def render_diffusion(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				text_map += str(int(self.game_state.diffusion[row][col])) + '\t'
			text_map += '\n'	
		return text_map
		
	# Render a text map of whats passable	
	def render_passable(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				if self.game_state.passable[row][col]:
					text_map += '.'
				else:
					text_map += '*'
			text_map += '\n'
		
		return text_map
	
	# Render a text map of whats occupied
	def render_occupy(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				if not self.game_state.occupied[row][col]:
					text_map += '.'
				else:
					text_map += '*'
			text_map += '\n'
		
		return text_map
	
	# Render a text map of the players seen
	def render_seen(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				if self.game_state.seen[row][col]:
					text_map += '.'
				else:
					text_map += '*'
			text_map += '\n'
		
		return text_map
	
	# Render a text map of the players vision
	def render_vision(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				if self.game_state.vision[row][col]:
					text_map += '.'
				else:
					text_map += '*'
			text_map += '\n'
		
		return text_map
		
	# Render a text map of the ratios
	def render_ratio(self):
		# Render the map
		text_map = ''
		
		for row in range(self.game_state.bucket_rows):
			for col in range(self.game_state.bucket_cols):
				threat = self.game_state.bucket_map[row][col].ratio
				if threat == 0:
					text_map += '.'
				elif threat < 0.2:
					text_map += 'o'
				elif threat < 0.5:
					text_map += 'O'
				elif threat < 0.8:
					text_map += '@'
				else:
					text_map += '#'	
			text_map += '\n'
			
		return text_map
	
	# Render a text map of enemies control
	def render_threat(self):
		# Render the map
		text_map = ''
		
		for row in range(self.game_state.bucket_rows):
			for col in range(self.game_state.bucket_cols):
				threat = self.game_state.bucket_map[row][col].threat
				if threat == 0:
					text_map += '.'
				elif threat < 5:
					text_map += 'o'
				elif threat < 10:
					text_map += 'O'
				elif threat < 50:
					text_map += '@'
				else:
					text_map += '#'	
			text_map += '\n'
			
		return text_map
		
	# Render a text map of my bots control
	def render_control(self):
		# Render the map
		text_map = ''
		
		for row in range(self.game_state.bucket_rows):
			for col in range(self.game_state.bucket_cols):
				control = self.game_state.bucket_map[row][col].control
				if control == 0:
					text_map += '.'
				elif control < 5:
					text_map += 'o'
				elif control < 10:
					text_map += 'O'
				elif control < 50:
					text_map += '@'
				else:
					text_map += '#'	
			text_map += '\n'
			
		return text_map
		
	# Render a text map of my ants density
	def render_my_density(self):
		# Render the map
		text_map = ''
		
		for row in range(self.game_state.bucket_rows):
			for col in range(self.game_state.bucket_cols):
				dense = self.game_state.bucket_map[row][col].my_density()
				if dense < 0.01:
					text_map += '.'
				elif dense < 0.1:
					text_map += 'o'
				elif dense < 0.3:
					text_map += 'O'
				elif dense < 0.6:
					text_map += '@'
				else:
					text_map += '#'	
			text_map += '\n'
			
		return text_map
		
	# Render a text map of enemies ants density
	def render_enemy_density(self):
		# Render the map
		text_map = ''
		for row in range(self.game_state.bucket_rows):
			for col in range(self.game_state.bucket_cols):
				dense = self.game_state.bucket_map[row][col].enemy_density()
				if dense < 0.01:
					text_map += '.'
				elif dense < 0.1:
					text_map += 'o'
				elif dense < 0.3:
					text_map += 'O'
				elif dense < 0.6:
					text_map += '@'
				else:
					text_map += '#'		
			text_map += '\n'
			
		return text_map
		
	# Render a text map of food density
	def render_food_density(self):
		# Render the map
		text_map = ''
		for row in range(self.game_state.bucket_rows):
			for col in range(self.game_state.bucket_cols):
				dense = self.game_state.bucket_map[row][col].food_density()
				if dense < 0.01:
					text_map += '.'
				elif dense < 0.1:
					text_map += 'o'
				elif dense < 0.3:
					text_map += 'O'
				elif dense < 0.6:
					text_map += '@'
				else:
					text_map += '#'				
			text_map += '\n'
			
		return text_map
		
	# Render a text map which bucket is which
	def render_find_bucket(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				text_map += str(self.game_state.find_bucket[row][col]) + '\t'
			text_map += '\n'	
		return text_map
		
	# Render a text map the retreat map
	def render_g_map(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				text_map += str(self.game_state.g_map[row][col]) + '\t'
			text_map += '\n'	
		return text_map
		
	# Render a text map of my hill scans
	def render_my_hill_scan(self):
		text_map = ''
		
		for hill_scan in self.game_state.my_hill_scans:
			text_map += 'Hill scan for location ' + str(hill_scan.loc) + '\n'
			for row in range(self.ants.rows):
				for col in range(self.ants.cols):
					text_map += str(hill_scan.g_map[row][col]) + '\t'
				text_map += '\n'
		return text_map
		
	# Render a text map of enemy hill scans
	def render_enemy_hill_scan(self):
		text_map = ''
		
		for hill_scan in self.game_state.enemy_hill_scans:
			text_map += 'Hill scan for location ' + str(hill_scan.loc) + '\n'
			for row in range(self.ants.rows):
				for col in range(self.ants.cols):
					text_map += str(hill_scan.g_map[row][col]) + '\t'
				text_map += '\n'
		return text_map
		
		
	# Render a text map of my attack range
	def render_my_attack(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				text_map += str(self.game_state.my_attack_map[row][col]) + '\t'
			text_map += '\n'	
		return text_map
		
	# Render a text map of the eney attack range
	def render_enemy_attack(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				text_map += str(self.game_state.enemy_attack_map[row][col]) + '\t'
			text_map += '\n'	
		return text_map
		
	# Render a text map of where fight is happening
	def render_fighting(self):
		text_map = ''
		
		for row in range(self.ants.rows):
			for col in range(self.ants.cols):
				text_map += str(self.game_state.fighting_map[row][col]) + '\t'
			text_map += '\n'	
		return text_map

		
	# Render a text map of the open closed A star
	def render_state(self, start, goal, open_list, closed_list):
		path_map = [ ['.'] * self.ants.rows for i in range(self.ants.cols)]
		text_map = ''
		
		# Draw all the open nodes
		for open in open_list:
			y, x = open.loc
			path_map[x][y] = 'O'
		
		#Draw all the closed nodes
		for closed in closed_list:
			y, x = closed.loc
			path_map[x][y] = 'C'

		#Draw the current start location
		y, x = start
		path_map[x][y] = 'S'
		
		#Draw the current start location
		y, x = goal
		path_map[x][y] = 'G'

		# Draw the whole map
		for x in range(self.ants.rows):
			for y in range(self.ants.cols):
				text_map += path_map[y][x]
			text_map += '\n'
		
		return text_map
		
	# Render a text map of the paths
	def render_paths(self):
		#Start with an empty
		path_map = [ ['.'] * self.ants.rows for i in range(self.ants.cols)]
		text_map = ''
		
		# Go through each path and each node
		x0 = y0 = x1 = y1 = 0
		for current in self.game_state.paths:
			for i in range(len(current.path)):
				if i == 0:
					continue
				
				# Get the path delta
				y0, x0 = current.path[i-1]
				y1, x1 = current.path[i]
				dx = x0 - x1
				dy = y0 - y1

				# Draw the direction the path is going
				if dx == 0:
					if dy == -1 or dy > 1:
						path_map[x0][y0] = 'v'
					if dy == 1 or dy < -1:
						path_map[x0][y0] = '^'
				else:
					if dx == -1 or dx > 1:
						path_map[x0][y0] = '>'
					if dx == 1 or dx < -1:
						path_map[x0][y0] = '<'
			
			# X Marks the spot
			if not i == 0:
				path_map[x1][y1] = 'X'

		# Render the map
		for x in range(self.ants.rows):
			for y in range(self.ants.cols):
				text_map += path_map[y][x]
			text_map += '\n'
		
		return text_map
