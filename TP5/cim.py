from typing import List
import math
import numpy as np

def zone_neighbours(zones, x_zone, y_zone, R, d, i, rc):
    neighbours = []
    for j in zones[y_zone][x_zone]:
        # Check if particle is overlapping with current particle
        if i != j and math.dist(R[i], R[j]) - (d[i] + d[j]) <= rc:
            neighbours.append(j)

    return neighbours

def get_neighbours(R, d, zy_size, zx_size, zy_count, zx_count, rc) -> List[List[int]]:
    particle_neighbours = [[] for _ in range(len(R))]
    
    # Initialize zones
    zones = [[[] for _ in range(zx_count)] for _ in range(zy_count)]

    # Add particles to zones
    for i in range(len(R)):
        x_zone = math.floor(R[i][0] / zx_size)
        y_zone = math.floor(R[i][1] / zy_size)
        zones[y_zone][x_zone].append(i)

    # Find neighbours of all particles
    for i in range(len(R)):

        x_zone = math.floor(R[i][0] / zx_size)
        y_zone = math.floor(R[i][1] / zy_size)

        zones_coords = [(x_zone, y_zone)]

        # Find top neighbours
        if y_zone > 0:
            zones_coords.append((x_zone, y_zone - 1))

        # Find bottom neighbours
        if y_zone < zy_count-1:
            zones_coords.append((x_zone, y_zone + 1))
        
        # Find left neighbours
        if x_zone > 0:
            zones_coords.append((x_zone - 1, y_zone))
        
        # Find right neighbours
        if x_zone < zx_count-1:
            zones_coords.append((x_zone + 1, y_zone))
        
        # Find top left neighbours
        if x_zone > 0 and y_zone > 0:
            zones_coords.append((x_zone - 1, y_zone - 1))
        
        # Find top right neighbours
        if x_zone < zx_count-1 and y_zone > 0:
            zones_coords.append((x_zone + 1, y_zone - 1))
        
        # Find bottom left neighbours
        if x_zone > 0 and y_zone < zy_count-1:
            zones_coords.append((x_zone - 1, y_zone + 1))

        # Find bottom right neighbours
        if x_zone < zx_count-1 and y_zone < zy_count-1:
            zones_coords.append((x_zone + 1, y_zone + 1))

        for coords in zones_coords:
            particle_neighbours[i] += zone_neighbours(zones, coords[0], coords[1], R, d, i, rc)

    return particle_neighbours

# 0.17
# 0.14
# 1.05