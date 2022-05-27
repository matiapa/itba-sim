from matplotlib import pyplot as plt
import numpy as np
from main import m, g, simulate, simulate_det, W, L


def energy_check():
    R0 = [np.array([W/4, L/4]), np.array([W/4+0.01, L*3/4])]
    D = [0.03, 0.02]
    R, V = simulate_det(R0, D, animate=True)
    # R, V = simulate(N=1, animate=True)

    up, uk, maxdiff = [], [], 0
    for s in range(len(R)):
        Rt, Vt = R[s], V[s]

        up.append( sum(m * g * Rt[:,1]) )
        
        uk.append( sum(0.5 * m * (Vt[:,0]**2+Vt[:,1]**2)) )

        diff = abs(up[-1]+uk[-1] - (up[0]+uk[0])) / (up[0]+uk[0])
        if diff > 0.01:
            print(f'{diff*100}% variation at step {s}')
        
        maxdiff = max(maxdiff, diff)

    print(f'Max energy variation: {maxdiff*100}%')
    
    steps = range(len(R))
    plt.plot(steps, up, label='Potential')
    plt.plot(steps, uk, label='Kinetic')
    plt.plot(steps, np.array(up) + np.array(uk), label='Total')

    plt.xlabel('Step')
    plt.ylabel('Energy (J)')
    plt.legend()
    plt.show()

energy_check()