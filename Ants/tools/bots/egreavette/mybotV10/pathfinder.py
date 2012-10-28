#!/usr/bin/env python
from ants import *
from a_node import *
from ai_state import *
from game_state import *
from render import *
import traceback

# Useful path finding function
class pathfinder():
    # Setup the class
	def __init__(self, ants, logger, game_state):
		self.ants = ants
		self.logger = logger
		self.game_state = game_state
		self.dir_rotation = [('n','e','s','w'),
							 ('e','s','w','n'),
							 ('s','w','n','e'),
							 ('w','n','e','s')]
		self.dir_astar = 0
		self.dir_hill = 0
		self.flood_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
		self.render = render(ants, self.game_state)
		
	# Basic A Star search 
	def a_star(self, start, goal, limit = 25):
		try:
			self.logger.debug("A star start: %s, goal: %s, limit: %d", start, goal, limit)
			heuristic = self.ants.distance(start, goal)
			start_node = a_node(start, None, heuristic, heuristic, heuristic)
			closed_list = []			# The closed list of searched nodes
			open_list = [start_node]	# The open list of searched nodes
			current = None
			row, col = start
			self.flood_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.flood_map[row][col] = True
			path = []
			
			# Keep going until we run out of an open nodes
			count = 0
			found = False
			while open_list:
				# Safety count
				count = count + 1
			
				# find the lowest f_score node
				current = open_list[0]
				for node in open_list:
					if current.f_score > node.f_score:
						current = node
						
				#self.logger.debug("Shortest node: %s, score: %d", current.loc, current.f_score)
			
				# Check if we have reached the goal
				if current.loc == goal or count > limit:				
					found = True
					break
					
				# Put the current node off the open and on the close list
				open_list.remove(current)
				closed_list.append(current)
				
				# Check all directions
				directions = self.dir_rotation[self.dir_astar]
				for direction in directions:
				
					# TODO inline destination
					new_loc = self.ants.destination(current.loc, direction)
					row, col = new_loc
					#self.logger.debug("Testing node: %s", new_loc)
							
					if self.flood_map[row][col]:
						continue				
					
					if not self.game_state.passable[row][col]:
						self.flood_map[row][col] = True
						continue

					#self.logger.debug("Node: %s PASSED", new_loc)
					b_loc = self.game_state.find_bucket[row][col]
					b_row, b_col = b_loc
					g_path = self.game_state.bucket_map[b_row][b_col].g_path
					
					# Make a new node
					g_score = current.g_score + g_path + 1
					h_score = self.ants.distance(new_loc, goal) * STAR_RATIO
					f_score = g_score + h_score
					new_node = a_node(new_loc, current, g_score, h_score, f_score)
					open_list.append(new_node)
					self.flood_map[row][col] = True
					
					#self.logger.debug("Added node: %s, f_score", new_loc, f_score)
			
			# If found then trace the path back
			if found:
				#self.logger.debug("Open list \n%s", open_list)
				#self.logger.debug("Closed list \n%s", closed_list)
				
				#self.logger.debug("A-Star state: \n%s", self.render.render_state(start, goal, open_list, closed_list))
				while current != None:
					#self.logger.debug("Current %s", current)
					#self.logger.debug("Current loc %s", current.loc)
					path.append(current.loc)
					current = current.parent								
				path.reverse()
			
			# Rotate a-star direction
			self.dir_astar += 1
			if self.dir_astar >= 4:
				self.dir_astar = 0
				
			return path
		except:
			self.logger.error("Error a-star: print_exc(): %s", traceback.format_exc())
		
	# A modified A Star search to quickly open up the ant hill for new ants
	def hill_push(self, start, limit):
		try:
			goal = (0,0)
			start_node = a_node(start, None, 0, 0, 0)
			closed_list = []			# The closed list of searched nodes
			open_list = [start_node]	# The open list of searched nodes

			row, col = start
			self.flood_map = [[False]*self.ants.cols for row in range(self.ants.rows)]
			self.flood_map[row][col] = True
			path = []
			
			# Keep going until we run out of an open nodes
			count = 0
			found = False
			while open_list:
				# Safety check
				count = count + 1
					
				# find the lowest g_score node
				lowest = 9999999
				for node in open_list:
					if lowest > node.g_score:
						lowest = node.g_score
						current = node
						
				# Check if we an unoccupied
				row, col = current.loc
				if not self.game_state.occupied[row][col] or count > limit:
					found = True
					break
					
				# Put the current node off the open and on the close list
				open_list.remove(current)
				closed_list.append(current)
				
				# Check all directions
				directions = self.dir_rotation[self.dir_hill]
				for direction in directions:
				
					# TODO inline destination
					new_loc = self.ants.destination(current.loc, direction)
					row, col = new_loc
					if self.flood_map[row][col]:
						continue				
					
					if not self.game_state.passable[row][col]:
						self.flood_map[row][col] = True
						continue
						
					if self.game_state.occupied[row][col] and not self.game_state.mobile_map[row][col]:
						self.flood_map[row][col] = True
						continue
					
					# Make a new node
					g_score = current.g_score + 1
					new_node = a_node(new_loc, current, g_score, 0, 0)
					open_list.append(new_node)
					self.flood_map[row][col] = True
			
			# If found then trace the path back
			if found:		
				while current != None:
					path.append(current.loc)
					current = current.parent								
				path.reverse()

			# Rotate hill direction
			self.dir_hill += 1
			if self.dir_hill >= 4:
				self.dir_hill = 0
			
			return path
		except:
			self.logger.error("Error hill push: print_exc(): %s", traceback.format_exc())