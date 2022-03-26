import matplotlib.pyplot as plt
from matplotlib import animation
from matplotlib.colors import ListedColormap
import numpy as np
import pandas as pd

df = pd.read_csv('output.csv')

grid, grid_size, img_plot = None, None, None
iteration = 0
df.set_index(['t'])

def initialize(size):
    global iteration

    grid = np.zeros((size, size))

    rows = df.loc[df['t'] == iteration].values
    iteration += 1
        
    for row in rows:
        grid[int(row[1]), int(row[2])] = int(row[3])

    return grid

def conway_step(frame):
    global grid, grid_size, img_plot, iteration
    if frame < 1:   # no movement for the first few steps
        new_grid = grid
    else:
        new_grid = np.zeros_like(grid)
        rows = df.loc[df['t'] == iteration].values
        iteration += 1
        
        for row in rows:
            new_grid[int(row[1]), int(row[2])] = int(row[3])
        grid = new_grid
    img_plot.set_data(new_grid)
    return img_plot,

def conway(random=True, size=8):
    global grid, grid_size, img_plot
    grid_size = size
    grid = initialize(size)
    fig, ax = plt.subplots(figsize=(8, 8))
    img_plot = ax.imshow(grid, interpolation='nearest', cmap=ListedColormap(['darkturquoise', 'yellow']))
    ax.set_xticks([])
    ax.set_yticks([])
    ani = animation.FuncAnimation(fig, frames=100, func=conway_step, interval=500)
    plt.tight_layout()
    ani.save('testconway.gif')
    plt.show()
    return ani

conway(size=8)