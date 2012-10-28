#!/usr/bin/env python
from game_state import *

# A class to scan the connectivity of the map.
class hill_scan():
    # Setup the class
	def __init__(self, game_state, loc, owner):
		self.ants = game_state.ants
		self.logger = game_state.logger
		self.game_state = game_state
		self.loc = loc
		self.owner = owner
		self.directions = ['n','e','s','w']
		self.flood_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
		self.g_map = [[-1]*self.ants.cols for row in range(self.ants.rows)]
		self.g_highest = 0	
		
		self.unseen_set = set()
		self.open_set = set()
		self.closed_set = set()
		
		self.open_set.add(loc)
		row, col = loc
		self.g_map[row][col] = 0
		self.flood_map[row][col] = True
	
	# Scan the surround area of a hill with wave expansion
	def scan(self, limit):
		try:
			# Copy any previously unseen nodes to the
			for unseen_loc in self.unseen_set.copy():
				row, col = unseen_loc
				
				# If the node has been seen then remove it from the unseen set
				if self.game_state.seen[row][col]:
					self.unseen_set.discard(unseen_loc)
					
					# If the node is passable add it to the open set
					if self.game_state.passable[row][col]:
						self.open_set.add(unseen_loc)
					else:
						self.g_map[row][col] = -1
						
			count = 0
			while self.open_set:
				# Check if we have reached the limit
				count += 1
				if count > limit:				
					break
			
				# Get the first location in the open set
				lowest = 9999999
				for loc in self.open_set:
					row, col = loc
					if self.g_map[row][col] < lowest:
						lowest = self.g_map[row][col]
						current = loc
						
				self.g_highest = lowest
					
				# Put the current node off the open and on the close set
				self.open_set.discard(current)
				self.closed_set.add(current)
				
				# Record g_score and increment for next square
				row, col = current
				g_score = self.g_map[row][col] + 1
				
				# Test the surrounding nodes 
				for direction in self.directions:
					
					# TODO inline destination
					new_loc = self.ants.destination(current, direction)
					row, col = new_loc
					#self.logger.debug("Testing node: %s", new_loc)
							
					if self.flood_map[row][col]:
						continue		

					self.flood_map[row][col] = True
					
					if not self.game_state.passable[row][col]:
						continue
						
					if not self.game_state.seen[row][col]:
						self.g_map[row][col] = g_score
						self.unseen_set.add(new_loc)
						continue
					
					self.g_map[row][col] = g_score
					self.open_set.add(new_loc)
				
		except:
			self.logger.error("Error scan map: print_exc(): %s", traceback.format_exc())
			
		