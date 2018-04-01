# SVG weirdness

- We could save time in rendering by not recreating the full feature path when panning the map
  by simply changing the viewbox

  BUT the coordinates will be very large (in tens of millions) and it hits some SVG problems
  (positions will vary, stroke widths inconsistent).

  So now we substract the top-left corner from all coordinates and have a consistent viewbox of "0 0 <w> <h>"