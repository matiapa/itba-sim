import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

rules_2D = ['Rule1112', 'Rule3323', 'Rule3623']
rules_3D = ['Rule2645', 'Rule5556', 'Rule6657']
rules = ['Rule1112', 'Rule3323', 'Rule3623', 'Rule2645', 'Rule5556', 'Rule6657']

def graph_radius_evolution(rules):
    df = pd.read_csv('stats_observables2.csv')
    df.set_index(['rule'])
    df.set_index(['p'])

    fig, axes = plt.subplots(2, 3)
    fig.subplots_adjust(hspace=0.3, wspace=0.3)

    for i in range(len(rules)):
        ax = axes[int(i/3)][i%3]

        # ax.set_yscale('log')
        ax.set_xlabel("Iteracion")
        ax.set_ylabel("Radio del patron")
        ax.title.set_text(rules[i])

        r_df = df.loc[df['rule'] == rules[i]]

        for p, p_df in r_df.groupby(['p']):
            ts, radius_avg, radius_std = [], [], []

            for t, t_df in p_df.groupby(['t']):
                ts.append( t )
                radius_avg.append( np.average(t_df['radius']) )
                radius_std.append( np.std(t_df['radius']) )

            ax.plot(ts, radius_avg, label=f'{p*100}%')
            # ax.errorbar(ts, radius_avg, radius_std, label=f'{p*100}%')

        ax.legend()

    plt.show()

def graph_mass_evolution(rules):
    df = pd.read_csv('stats_observables.csv')
    df.set_index(['rule'])
    df.set_index(['p'])

    fig, axes = plt.subplots(2, 3)
    fig.subplots_adjust(hspace=0.3, wspace=0.3)

    for i in range(len(rules)):
        ax = axes[int(i/3)][i%3]
        # ax.set_yscale('log')
        ax.set_xlabel("Iteracion")
        ax.set_ylabel("Masa del patron")
        ax.title.set_text(rules[i])

        r_df = df.loc[df['rule'] == rules[i]]

        for p, p_df in r_df.groupby(['p']):
            ts, mass_avg, mass_std = [], [], []

            for t, t_df in p_df.groupby(['p']):
                ts.append( t )
                mass_avg.append( np.average(t_df['mass']) )
                mass_std.append( np.std(t_df['mass']) )

            ax.errorbar(ts, mass_avg, mass_std, label=f'{p*100}%')

        ax.legend()

    plt.show()


def graph_stable_observables(file, rules):
    df = pd.read_csv(file)
    df.set_index(['rule'])
    df.set_index(['p'])

    fig, axes = plt.subplots(2, 3)
    fig.subplots_adjust(hspace=0.3, wspace=0.3)

    for i in range(len(rules)):
        ax = axes[0][i]
        ax.set_xlabel("Proporcion de celdas vivas inicialmente")
        ax.set_ylabel("Valor estable del radio")
        ax.title.set_text(rules[i])

        selected_data = df.loc[df['rule'] == rules[i]]
        stable_radius_avg, stable_radius_std = [], []

        for p, grp in selected_data.groupby(['p']):
            iterations = len(grp['avgMaxRadius'])     

            radius_avg = grp['avgMaxRadius'][int(0.75*iterations):]
            stable_radius_avg.append( np.average(radius_avg) )

            radius_std = np.average(grp['stdMaxRadius'][int(0.75*iterations):])
            stable_radius_std.append( np.average(radius_std) )

        ax.errorbar(np.unique(selected_data['p']), stable_radius_avg, stable_radius_std)

    for i in range(len(rules)):
        ax = axes[1][i]
        ax.set_xlabel("Proporcion de celdas vivas inicialmente")
        ax.set_ylabel("Valor estable de la masa")
        ax.title.set_text(rules[i])

        selected_data = df.loc[df['rule'] == rules[i]]
        stable_mass_avg, stable_mass_std = [], []

        for p, grp in selected_data.groupby(['p']):
            iterations = len(grp['avgAliveCells'])

            mass_avg = grp['avgAliveCells'][int(0.75*iterations):]
            stable_mass_avg.append( np.average(mass_avg) )

            mass_std = grp['stdAliveCells'][int(0.75*iterations):]
            stable_mass_std.append( np.average(mass_std) )

        ax.errorbar(np.unique(selected_data['p']), stable_mass_avg, stable_mass_std)

    plt.show()


def graph_step_observables(file, rules):
    df = pd.read_csv(file)
    df.set_index(['rule'])
    df.set_index(['p'])

    fig, axes = plt.subplots(2, 3)
    fig.subplots_adjust(hspace=0.3, wspace=0.3)

    for i in range(len(rules)):
        ax = axes[0][i]
        ax.set_xlabel("Proporcion de celdas vivas inicialmente")
        ax.set_ylabel("Pendiente del radio")
        ax.title.set_text(rules[i])

        selected_data = df.loc[df['rule'] == rules[i]]
        radius_step_avg, radius_step_std = [], []

        for p, grp in selected_data.groupby(['p']):
            iterations = len(grp['avgMaxRadius'].to_numpy())

            final_radius_avg = grp['avgMaxRadius'].to_numpy()[iterations-1]
            init_radius_avg = grp['avgMaxRadius'].to_numpy()[0]
            radius_step_avg.append( (final_radius_avg - init_radius_avg) / iterations )

            final_radius_std = grp['stdMaxRadius'].to_numpy()[iterations-1]
            init_radius_std = grp['stdMaxRadius'].to_numpy()[0]
            radius_step_std.append( (final_radius_std - init_radius_std) / iterations )

        ax.errorbar(np.unique(selected_data['p']), radius_step_avg, radius_step_std)

    for i in range(len(rules)):
        ax = axes[1][i]
        ax.set_xlabel("Proporcion de celdas vivas inicialmente")
        ax.set_ylabel("Pendiente de la masa")
        ax.title.set_text(rules[i])

        selected_data = df.loc[df['rule'] == rules[i]]
        mass_step_avg, mass_step_std = [], []

        for p, grp in selected_data.groupby(['p']):
            iterations = len(grp['avgAliveCells'].to_numpy())

            final_mass_avg = grp['avgAliveCells'].to_numpy()[iterations-1]
            init_mass_avg = grp['avgAliveCells'].to_numpy()[0]
            mass_step_avg.append( np.average((final_mass_avg - init_mass_avg) / iterations) )

            final_mass_std = grp['stdMaxRadius'].to_numpy()[iterations-1]
            init_mass_std = grp['stdMaxRadius'].to_numpy()[0]
            mass_step_std.append( (final_mass_std - init_mass_std) / iterations )

        ax.errorbar(np.unique(selected_data['p']), mass_step_avg, mass_step_std)

    plt.show()

# graph_stable_observables('stats_by_t_2D.csv', ['Rule3323'])
# graph_stable_observables('stats_by_t_3D.csv', ['Rule5556', 'Rule6657'])

# graph_step_observables('stats_by_t_2D.csv', ['Rule1112', 'Rule3323', 'Rule3623'])

graph_radius_evolution(rules)


# Linear radius: 1112 (2D), 3323 (2D), 3623 (2D), 2645 (3D)
# Stable radius: 5556 (3D), 6657 (3D)

# Linear mass: 1112 (2D), 3623 (2D), 2645 (3D)
# Stable mass: 3323 (2D), 5556 (3D), 6657 (3D)

# 2D
# 1112: LR, LM
# 3323: LR, SM
# 3623: LR, LM

# 3D
# 2645: LR, LM
# 5556: SR, SM
# 6657: SR, SM