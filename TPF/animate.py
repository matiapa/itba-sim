import sys
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import animation
from tqdm import tqdm

# ------------------------------------ 2D ANIMATION ------------------------------------

state_colors = {
    "SUSCEPTIBLE": [0,0,1], "EXPOSED": [1,1,0], "INFECTED": [1,0,0], "QUARANTINED": [0.5,0.5,0.5],
    "RECOVERED": [0,1,0], "DEAD": [1,1,1]
}

def create_grid(size, iteration):
    grid = np.ones((size, size, 3))

    rows = df.loc[df['t'] == iteration].values
    for row in rows:
        grid[int(row[1]), int(row[2]), :] = state_colors[row[3]]

    return grid

def animate_step(frame):
    global grid_size, img_plot, load_iter, ax

    if frame > 0:
        new_grid = create_grid(grid_size, frame) 
        img_plot.set_data(new_grid)

    # plt.savefig(f"out/{frame}.png")

    try:
        load_iter.__next__()
    except:
        pass

def plot(size, animate=False):
    global grid_size, img_plot

    fig, ax = plt.subplots(figsize=(8, 8))

    ax.set_xlabel("y")
    ax.set_ylabel("x")

    ax.set_xticks(np.arange(0, grid_size, 1), minor=True)
    ax.set_yticks(np.arange(0, grid_size, 1), minor=True)

    plt.tight_layout()

    grid_size = size
    grid = create_grid(size, 0)
    img_plot = ax.imshow(grid)

    if animate:
        anim = animation.FuncAnimation(fig, frames=end_t, func=animate_step, interval=1)
        anim.save('out/animation.gif')
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

plot(size = grid_size, animate = animate)