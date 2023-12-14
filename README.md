Tool to do flipbook-style SVG animation with layers in Inkscape and other SVG editors.

## [Try it out online](https://svgflipbook.com/)

![SVG Animation Assistant interface showing Inkscape and a walk cycle animation](./screens/walk-cycle.gif)

This tool will cycle through the layers of your SVG allowing you to do basic flip-book style animation. Each layer in your SVG is one frame of the animation.

The animation live-reloads in the assistant window whenever you hit save in Inkscape.

![SVG Animation Assistant interface showing live reloading](./screens/svg-animation-assistant.gif)

Customise the frame time and behaviour by editing the layer name:

![Inkscape layers UI with customisation](./screens/layers.png)

 * Set the number of milliseconds to pause on each frame by entering a number in brackets in the layer name like (100) for a pause of 1/10th of a second.
 * Add static background frames by putting (static) in the layer name.
