import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import numpy as np
import pandas as pd

df = pd.read_csv('out.csv')
df.set_index(['t'])

fig = plt.figure(figsize=(6, 6))
ax = plt.axes(xlim=(0, 6),ylim=(0, 6))

scatter = ax.scatter(df.loc[df['t'] == 0]['x'], df.loc[df['t'] == 0]['y'])

def update(t):
    data = df.loc[df['t'] == t][['x','y']].to_numpy()
    scatter.set_offsets(data)
    return scatter,

anim = FuncAnimation(fig, update, frames=set(df['t']), interval=500)
anim.save('animation.gif')