#!/usr/bin/env python
from ai_state import *

# A path path class that contains the path list and target
class path():
    # Setup the class
	def __init__(self, path, step):
		self.path = path
		self.length = len(path)
		self.start = path[0]
		self.target = path[-1]
		self.max_count = int(self.length * PATH_DURATION)
		self.count = 0
		self.step = step
		self.cycle_step = 0
		self.score_step = (PATH_END - PATH_BEGIN) / self.length