import kivy
from kivy.app import App
from kivy.uix.image import Image
import pandas as pd
import os
import matplotlib.pyplot as plt
from pandas.plotting import table 
from kivy.core.window import Window


def initState():
    #os.chdir(r'data')
    df = pd.read_csv('./data/android_version.csv')
    df2 = df.iloc[[12]]
    df2 = df2.drop(['Date'], axis=1)
    pie = df2.plot(kind="hist", figsize=(5,20), legend = True, use_index=True, subplots=True, colormap="Pastel1")
    fig = pie[0].get_figure()
    fig.savefig('myhist.png')

class MainApp(App):
    Window.clearcolor = (1, 1, 1, 1)
    initState()
    def build(self):
	image = Image(allow_stretch = True, source="myhist.png")
        return image

if __name__ == '__main__':
    app = MainApp()
    app.run()
