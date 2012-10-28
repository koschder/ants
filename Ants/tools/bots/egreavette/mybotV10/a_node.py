#!/usr/bin/env python

# A node in an a star search
class a_node():
    # Setup the class
	def __init__(self, loc, parent = None, g_score = 0, h_score = 0, f_score = 0):
		self.loc = loc			# Tuple location of the node in the map
		self.parent = parent	# Reference to the parent node for transversal
		self.g_score = g_score	# The move cost from the start
		self.h_score = h_score	# The heuristic cost of the node to the goal
		self.f_score = f_score	# The total of g_score and h_score