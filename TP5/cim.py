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
        x_zone = math.floor(R[i][0] / zx_size) if R[i][0] > zx_size else 0
        y_zone = math.floor(R[i][1] / zy_size) if R[i][1] > zy_size else 0

        if x_zone > zx_count - 1 or y_zone > zy_count - 1 or x_zone < 0 or y_zone < 0:
            print(R[i][0] / zx_size, R[i][1] / zy_size, x_zone, y_zone, zx_count, zy_count, R[i][0], R[i][1])
        zones[y_zone][x_zone].append(i)

    # Get neighbours
        # print("{}: Coordinates {} -> zone {} -> {} , {} -> {}     -     neighbours: {}".format(i, R[i], R[i][0] / zone_x_size, x_zone, R[i][1] / zone_y_size, y_zone, zones[y_zone][x_zone]))

    # print("------------ Zones ------------")
    # for i in range(len(zones)):
    #     print(zones[i])
    # print("--------------------------------")

    # Find neighbours of all particles
    for i in range(len(R)):

        x_zone = math.floor(R[i][0] / zx_size)
        y_zone = math.floor(R[i][1] / zy_size)

        zones_coords = []

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
            particle_neighbours[i].extend(zone_neighbours(zones, coords[0], coords[1], R, d, i, rc))

        particle_neighbours[i].extend(zone_neighbours(zones, x_zone, y_zone, R, d, i, rc))

    # print("------------- Neighbours -------------")
    # for i in range(len(particle_neighbours)):
    #     print(f'{i}: {particle_neighbours[i]}')

    return particle_neighbours