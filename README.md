# ComposeSnapHelper

Snap helper mostly based on [ComposePagerSnapHelper](https://github.com/aakarshrestha/compose-pager-snap-helper). 

It helps to snap to the central item, similiar to PagerSnalHelper or LinearSnapHelper. 

Usage example:

```kotlin
ComposeCenterSnapHelper(parentSize = getScreenWidth()) { state ->
    LazyRow(state = state) {
        items(count = 15) { i ->
            Text("Item #$i")
        }
    }
}
```
