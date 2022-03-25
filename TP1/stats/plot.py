import matplotlib.pyplot as plt
import numpy as np
from matplotlib.collections import EllipseCollection
import pandas as pd
import math 

df = pd.read_csv('../output.csv', sep=',')

colored_neighbours = list()
x = df['x']
y = df['y']
size = math.pi*df['r'] + 10
r = df['r']
neighbours = df['neighbours']
cmap = plt.cm.hsv

fig, ax = plt.subplots()
sc = ax.scatter(x, y, s=size, cmap=cmap, picker=True)

offsets = list(zip(x, y))
for i, point in df.iterrows():
    ax.text(point['x'], point['y'], str(point['ID']))

plt.grid(color = 'grey', linestyle = '--', linewidth = 0.5)

annot = ax.annotate("", xy=(0,0), xytext=(20,20),textcoords="offset points",
                    bbox=dict(boxstyle="round", fc="w"),
                    arrowprops=dict(arrowstyle="->"))
annot.set_visible(False)

def update_annot(ind):
    index = int(ind["ind"])
    pos = sc.get_offsets()[ind["ind"][0]]
    annot.xy = pos
    text = "id: {}\nr: {}\nneighbours: {}\nx: {}\ny: {}".format(index, r[index], neighbours[index], x[index], y[index])
    annot.color = 'r'
    annot.set_text(text)
    ax.scatter(pos[0], pos[1], color='r', s=size[index])
    # if len(colored_neighbours) > 0:
    for i in colored_neighbours:
        if i != index:
            ax.scatter(x[int(i)], y[int(i)], color='b', s=size[int(i)])

    colored_neighbours.clear()
    colored_neighbours.append(index)

    neighbours_list = neighbours[index].replace('[', '').replace(']', '').split(',')
    for i in neighbours_list:
        ax.scatter(x[int(i)], y[int(i)], color='r', s=size[int(i)])
        colored_neighbours.append(i)
    

def hover(event):
    vis = annot.get_visible()
    if event.inaxes == ax:
        cont, ind = sc.contains(event)
        if cont:
            update_annot(ind)
            annot.set_visible(True)
            fig.canvas.draw_idle()
        else:
            if vis:
                annot.set_visible(False)
                fig.canvas.draw_idle()

# fig.canvas.mpl_connect('pick_event', on_pick)

fig.canvas.mpl_connect("motion_notify_event", hover)

plt.show()