import pandas as pd
import matplotlib.pyplot as plt

processed_data = pd.read_csv('stats_by_t.csv')
processed_data.set_index(['rule'])

fig, (ax1, ax2) = plt.subplots(2)
for key, grp in processed_data.groupby(['rule']):
    # set x axes labels to plot
    ax1.set_xlabel("Iteration")
    ax1.set_ylabel("Alive Cells")
    ax1 = grp.plot(ax=ax1, kind='line', x='t', y='avgAliveCells', label=key)
    ax2.set_xlabel("Iteration")
    ax2.set_ylabel("Radius")
    ax2 = grp.plot(ax=ax2, kind='line', x='t', y='avgMaxRadius', label=key)

plt.show()