import matplotlib
import matplotlib.pyplot as plt
from matplotlib import animation
from matplotlib.colors import ListedColormap
import numpy as np
import pandas as pd
from dotenv import load_dotenv
import os
import sys

load_dotenv()

matplotlib.rcParams['animation.ffmpeg_path'] = os.getenv('ffmpeg_path')

# ------------------------------------ 2D ANIMATION ------------------------------------

def create_grid_2D(size, iteration):
    grid = np.zeros((size, size))

    rows = df.loc[df['t'] == iteration].values
    for row in rows:
        grid[int(row[1]), int(row[2])] = int(row[3])

    return grid

def animate_step_2D(frame):
    global grid_size, img_plot

    if frame > 0:
        new_grid = create_grid_2D(grid_size, frame)    
        img_plot.set_data(new_grid)

    plt.savefig(f'out/{frame}.png')

    return img_plot,

def plot_2D(size, animate=False):
    global grid_size, img_plot

    fig, ax = plt.subplots(figsize=(8, 8))

    ax.set_xlabel("y")
    ax.set_ylabel("x")

    ax.set_xticks(np.arange(0, grid_size, 1), minor=True)
    ax.set_yticks(np.arange(0, grid_size, 1), minor=True)
    # ax.grid(which='minor', color='grey', linestyle='-', linewidth=2)

    plt.tight_layout()

    grid_size = size
    grid = create_grid_2D(size, 0)
    img_plot = ax.imshow(grid, interpolation='nearest', cmap=ListedColormap(['black', 'white']))

    if animate:
        anim = animation.FuncAnimation(fig, frames=end_t, func=animate_step_2D, interval=500)
        video_writer = animation.FFMpegWriter(fps=2)
        anim.save('out/animation_2D.mp4', writer=video_writer)
    else:
        plt.show()

# ------------------------------------ 3D ANIMATION ------------------------------------

def create_grid_3D(size, iteration):
    grid = np.zeros((size, size, size))

    rows = df.loc[df['t'] == iteration].values
    for row in rows:
        grid[int(row[1]), int(row[2]), int(row[3])] = int(row[4])

    return grid

def animate_step_3D(frame):
    global grid_size, ax

    if frame > 0:
        filled = create_grid_3D(grid_size, frame)

        ax.clear()
        ax.set_xlabel("x")
        ax.set_ylabel("y")
        ax.set_zlabel("z")
        ax.voxels(filled, edgecolors='gray', shade=False)

    plt.savefig(f'out/{frame}.png')

def plot_3D(size, animate=False):
    global grid_size, img_plot, ax

    fig = plt.figure()
    ax = fig.add_subplot(projection='3d')
    
    ax.set_xlabel("x")
    ax.set_ylabel("y")
    ax.set_zlabel("z")
    ax.grid(True)

    plt.tight_layout()

    grid_size = size
    filled = create_grid_3D(size, 0)
    img_plot = ax.voxels(filled, edgecolors='gray', shade=False)

    if animate:
        anim = animation.FuncAnimation(fig, frames=end_t, func=animate_step_3D, interval=500)
        video_writer = animation.FFMpegWriter(fps=2)
        anim.save('out/animation_3D.mp4', writer=video_writer)
    else:
        plt.show()

# ------------------------------------ MAIN CODE ------------------------------------

df = pd.read_csv('output.csv')
df.set_index(['t'])

grid_size = 100
end_t = max(df['t'])

if sys.argv[1] == 'animate':
    animate = True
elif sys.argv[1] == 'snap':
    animate = False
else:
    raise f'Unsupported operation {sys.argv[1]}'

if 'z' in df.columns:
    plot_3D(size = grid_size, animate = animate)
else:
    plot_2D(size = grid_size, animate = animate)

df.columns