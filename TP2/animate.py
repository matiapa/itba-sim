import os
import sys
import numpy as np
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
from matplotlib import animation
from matplotlib.colors import ListedColormap, LinearSegmentedColormap
from dotenv import load_dotenv
from tqdm import tqdm

load_dotenv()

matplotlib.rcParams['animation.ffmpeg_path'] = os.getenv('ffmpeg_path')

# ------------------------------------ 2D ANIMATION ------------------------------------

def create_grid_2D(size, iteration):
    grid = np.zeros((size, size))

    max_distance = np.sqrt((size/2)**2 + (size/2)**2)

    rows = df.loc[df['t'] == iteration].values
    for row in rows:
        grid[int(row[1]), int(row[2])] = np.sqrt((int(row[1]) - size/2)**2 + (int(row[2]) - size/2)**2) / max_distance

    return grid

def animate_step_2D(frame):
    global grid_size, img_plot, load_iter, ax

    if frame > 0:
        new_grid = create_grid_2D(grid_size, frame) 
        img_plot.set_data(new_grid)

    try:
        load_iter.__next__()
    except:
        pass

def plot_2D(size, animate=False):
    global grid_size, img_plot

    fig, ax = plt.subplots(figsize=(8, 8))

    ax.set_xlabel("y")
    ax.set_ylabel("x")

    ax.set_xticks(np.arange(0, grid_size, 1), minor=True)
    ax.set_yticks(np.arange(0, grid_size, 1), minor=True)

    plt.tight_layout()

    grid_size = size
    grid = create_grid_2D(size, 0)
    cmap = LinearSegmentedColormap.from_list('my_colormap', [(0, '#ffffff'),(0.0000000001, '#ff0000'), (0.4, '#ff9900'), (0.7, '#ffe135'), (1, '#00cd00')])   
    img_plot = ax.imshow(grid, interpolation='nearest', cmap=cmap)

    if animate:
        anim = animation.FuncAnimation(fig, frames=end_t, func=animate_step_2D, interval=300)
        # anim.save('animation_2D.gif')
        video_writer = animation.FFMpegWriter(fps=2)
        anim.save('out/animation_2D.mp4', writer=video_writer)
    else:
        plt.show()

# ------------------------------------ 3D ANIMATION ------------------------------------

def create_grid_3D(size, iteration):
    grid = np.zeros((size, size, size))
    colors = np.zeros((size, size, size))

    max_distance = np.sqrt((size/2)**2 + (size/2)**2 + (size/2)**2)

    rows = df.loc[df['t'] == iteration].values
    for row in rows:
        grid[int(row[1]), int(row[2]), int(row[3])] = np.sqrt((int(row[1]) - size/2)**2 + (int(row[2]) - size/2)**2 + (int(row[3]) - size/2)**2) / max_distance
        colors[int(row[1]), int(row[2]), int(row[3])] = np.sqrt((int(row[1]) - size/2)**2 + (int(row[2]) - size/2)**2 + (int(row[3]) - size/2)**2) / max_distance


    return grid, colors

def animate_step_3D(frame):
    global grid_size, ax, load_iter

    if frame > 0:
        filled, colors = create_grid_3D(grid_size, frame)

        ax.clear()
        ax.set_xlabel("x")
        ax.set_ylabel("y")
        ax.set_zlabel("z")
        ax.voxels(filled, edgecolors='gray', shade=False, facecolors=plt.cm.viridis(colors))

    try:
        load_iter.__next__()
    except:
        pass

def plot_3D(size, animate=False):
    global grid_size, img_plot, ax

    distances_to_center = list()
    colors = list()
    voxelarray = None
    first_time = True

    max_distance = np.sqrt((grid_size/2)**2 + (grid_size/2)**2 + (grid_size/2)**2)

    fig = plt.figure()
    ax = fig.add_subplot(projection='3d')
    
    ax.set_xlabel("x")
    ax.set_ylabel("y")
    ax.set_zlabel("z")
    ax.grid(True)

    plt.tight_layout()

    grid_size = size
    filled, colors = create_grid_3D(size, 0)
    img_plot = ax.voxels(filled, edgecolors='gray', shade=False, facecolors=plt.cm.viridis(colors))

    if animate:
        anim = animation.FuncAnimation(fig, frames=end_t, func=animate_step_3D, interval=300)
        # anim.save('animation_3D.gif')
        video_writer = animation.FFMpegWriter(fps=2)
        anim.save('out/animation_3D.mp4', writer=video_writer)
    else:
        plt.show()

# ------------------------------------ MAIN CODE ------------------------------------

df = pd.read_csv('output.csv')
df.set_index(['t'])

grid_size = 100
end_t = max(df['t'])

load_iter = tqdm(range(end_t)).__iter__()

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