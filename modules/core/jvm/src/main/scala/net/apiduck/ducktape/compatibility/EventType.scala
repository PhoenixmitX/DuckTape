package net.apiduck.ducktape.compatibility

object EventType:

  type Event = MouseEvent | KeyboardEvent | InputEvent | FocusEvent | WheelEvent | PointerEvent | TouchEvent | DragEvent

  type MouseEvent = "MouseEvent"
  type KeyboardEvent = "KeyboardEvent"
  type InputEvent = "InputEvent"
  type FocusEvent = "FocusEvent"
  type WheelEvent = "WheelEvent"
  type PointerEvent = "PointerEvent"
  type TouchEvent = "TouchEvent"
  type DragEvent = "DragEvent"
