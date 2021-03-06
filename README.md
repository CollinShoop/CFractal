# CFractal

Generate simple 
[line based geometric fractals](https://www.stsci.edu/~lbradley/seminar/fractals.html#:~:text=A%20one%20dimensional%20line%20segment,of%20half%20the%20original%20length)
using an OK looking GUI and controls. 

## Running

```
mvn clean package
java -jar target/cfractal-1.0-SNAPSHOT.jar
```

## Controls

- Drag the mouse to move around
- Mouse scroll wheel to zoom in/out
- `+` to increase iteration depth
- `-` to decrease iteration depth

## Examples

### Sierpinski Triangle

There's more than a single way to construct some fractals. For example, the [Sierpinski Triangle](https://en.wikipedia.org/wiki/Sierpi%C5%84ski_triangle). 

This version has the benefit of being constructed from a single, flowing, series of connected lines. 

![](img/sierpinski-shallow.png)

Increasing the depth a few levels yields a sharp and colored in result. 

![](img/sierpinski-deep.png)

### Other

![](img/example-2.png)
