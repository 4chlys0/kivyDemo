import kivy
from kivy.app import App
from kivy.uix.image import Image
import pandas as pd
from kivy.core.window import Window


def initState():
    df = pd.read_csv('./data/android_version.csv')
    #Keep only latest data set
    df2 = df.iloc[[12]]
    #Remove date for graphing
    df2 = df2.drop(['Date'], axis=1)
    #Simple histogram
    hist = df2.plot(kind="hist", legend = True, subplots=True, title="Android OS Version Fragmentation", colormap="Pastel1")
    fig = hist[0].get_figure()
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
