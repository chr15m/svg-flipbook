# Illustrator SVG Files

## Process

1. Create new file in illustrator
2. Create new layers
3. File > Save As... Format: SVG (default settings)

## Observations

Illustrator names layers i the following format `Layer 1`, `Layer 2`, `Layer 3`.

On save illustrator renderes each layer into a `<g></g>`

When re-opening the SVG file in illustrator all groups are nested under a parent layer node `Layer 1`.

Layer 1
├── SVG Group 1
├── SVG Group 2
└── SVG Group 3

For simplicity new layers should be nested below the parent layer node.
