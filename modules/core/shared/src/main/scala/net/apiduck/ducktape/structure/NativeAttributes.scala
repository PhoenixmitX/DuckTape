package net.apiduck.ducktape.render.native

import net.apiduck.ducktape.compatibility.ElementType.*
import net.apiduck.ducktape.types.AttributeType
import net.apiduck.ducktape.compatibility.EventType
import net.apiduck.ducktape.compatibility.EventType.Event

trait NativeAttributes:
  /**
    * Specifies the types of files that the server accepts (only for type="file")
    */
  val accept = AttributeType.String[InputElement]("accept")

  /**
    * Specifies the character encodings that are to be used for the form submission
    */
  val acceptCharset = AttributeType.String[FormElement]("accept-charset")

  /**
    * Specifies a shortcut key to activate/focus an element
    */
  val accessKey = AttributeType.String("accesskey")

  /**
    * Specifies where to send the form-data when a form is submitted
    */
  val action = AttributeType.String[FormElement]("action")

  /**
    * Specifies an alternate text when the original element fails to display
    */
  val alt = AttributeType.String[AreaElement | ImageElement | InputElement]("alt")

  /**
    * Specifies that the script is executed asynchronously (only for external scripts)
    */
  val async = AttributeType.Boolean[ScriptElement]("async")

  /**
    * Specifies whether the [FormElement] or the [InputElement] element should have autocomplete enabled
    */
  val autoComplete = AttributeType.Boolean[FormElement | InputElement]("autocomplete")

  /**
    * Specifies that the element should automatically get focus when the page loads
    */
  val autoFocus = AttributeType.Boolean[ButtonElement | InputElement | SelectElement | TextAreaElement]("autofocus")

  /**
    * Specifies that the audio/video will start playing as soon as it is ready
    */
  val autoPlay = AttributeType.Boolean[AudioElement | VideoElement]("autoplay")

  /**
    * Specifies the character encoding
    */
  val charset = AttributeType.String[MetaElement | ScriptElement]("charset")

  /**
    * Specifies that an [InputElement] element should be pre-selected when the page loads (for type="checkbox" or type="radio")
    */
  val checked = AttributeType.Boolean[InputElement]("checked")

  /**
    * Specifies a URL which explains the quote/deleted/inserted text
    */
  val cite = AttributeType.String[QuoteElement | ModElement]("cite")

  /**
    * Specifies one or more classnames for an element (refers to a class in a style sheet)
    */
  val `class` = AttributeType.String("class")
  val classNames = `class`

  /**
    * Specifies the visible width of a text area
    */
  val cols = AttributeType.Number[TextAreaElement]("cols")

  /**
    * Specifies the number of columns a table cell should span
    */
  val colSpan = AttributeType.Number[TableCellElement]("colspan")

  /**
    * Gives the value associated with the http-equiv or name attribute
    */
  val content = AttributeType.String[MetaElement]("content")

  /**
    * Specifies whether the content of an element is editable or not
    */
  val contentEditable = AttributeType.Boolean("contenteditable")

  /**
    * Specifies that audio/video controls should be displayed (such as a play/pause button etc)
    */
  val controls = AttributeType.Boolean[AudioElement | VideoElement]("controls")

  /**
    * Specifies the coordinates of the area
    */
  val coords = AttributeType.String[AreaElement]("coords")

  /**
    * Specifies the URL of the resource to be used by the object
    */
  val data = AttributeType.String[ObjectElement]("data")


  // TODO: data-* attributes
  // Used to store custom data private to the page or application
  // val data-* = AttributeType.String("data-${???}")

  /**
    * Specifies the date and time
    */
  val dateTime = AttributeType.String[ModElement /* | TimeElement */]("datetime") // TODO HTMLTimeElement

  /**
    * Specifies that the track is to be enabled if the user's preferences do not indicate that another track would be more appropriate
    */
  val default = AttributeType.Boolean[TrackElement]("default")

  /**
    * Specifies that the script is executed when the page has finished parsing (only for external scripts)
    */
  val defer = AttributeType.Boolean[ScriptElement]("defer")

  /**
    * Specifies the text direction for the content in an element
    */
  val dir = AttributeType.String("dir")

  /**
    * Specifies that the text direction will be submitted
    */
  val dirName = AttributeType.String[InputElement | TextAreaElement]("dirname")

  /**
    * Specifies that the specified element/group of elements should be disabled
    */
  val disabled = AttributeType.Boolean[ButtonElement | FieldSetElement | InputElement | OptGroupElement | OptionElement | SelectElement | TextAreaElement]("disabled")

  /**
    * Specifies that the target will be downloaded when a user clicks on the hyperlink
    */
  val download = AttributeType.Boolean[AnchorElement | AreaElement]("download")

  /**
    * Specifies whether an element is draggable or not
    */
  val draggable = AttributeType.Boolean("draggable")

  /**
    * Specifies how the form-data should be encoded when submitting it to the server (only for method="post")
    */
  val encType = AttributeType.String[FormElement]("enctype")

  /**
    * Specifies the text of the enter-key on a virtual keybord
    */
  val enterKeyHint = AttributeType.String("enterkeyhint")

  /**
    * Specifies which form element(s) a label/calculation is bound to
    */
  val `for` = AttributeType.String[LabelElement /* | OutputElement */]("for") // TODO HTMLOutputElement

    /**
     * Specifies the name of the form the element belongs to
     */
  val form = AttributeType.String[ButtonElement | FieldSetElement | InputElement | LabelElement /* | MeterElement */ | ObjectElement /* | OutputElement */ | SelectElement | TextAreaElement]("form") // TODO HTMLMeterElement, HTMLOutputElement

  /**
    * Specifies where to send the form-data when a form is submitted. Only for type="submit"
    */
  val formAction = AttributeType.String[ButtonElement | InputElement]("formaction")

  /**
    * Specifies one or more headers cells a cell is related to
    */
  val headers = AttributeType.String[TableCellElement]("headers")

  /**
    * Specifies the height of the element
    */
  val height = AttributeType.Number[CanvasElement | EmbedElement | ImageElement | InputElement | ObjectElement | VideoElement]("height")

  /**
    * Specifies that an element is not yet, or is no longer, relevant
    */
  val hidden = AttributeType.Boolean("hidden")

  /**
    * Specifies the range that is considered to be a high value
    */
  val high = AttributeType.Number/* [MeterElement] */("high") // TODO HTMLMeterElement

  /**
    * Specifies the URL of the page the link goes to
    */
  val href = AttributeType.String[AnchorElement | AreaElement | BaseElement | LinkElement]("href")

  /**
    * Specifies the language of the linked document
    */
  val hrefLang = AttributeType.String[AnchorElement | AreaElement | LinkElement]("hreflang")

    // Provides an HTTP header for the information/value of the content attribute
  val httpEquiv = AttributeType.String[MetaElement]("http-equiv")

  /**
    * Specifies a unique id for an element
    */
  val id = AttributeType.String("id")

  /**
    * Specifies that the browser should ignore this section
    */
  val inert = AttributeType.Boolean("inert")

  /**
    * Specifies the mode of a virtual keyboard
    */
  val inputMode = AttributeType.String("inputmode")

  /**
    * Specifies an image as a server-side image map
    */
  val isMap = AttributeType.Boolean[ImageElement]("ismap")

  /**
    * Specifies the kind of text track
    */
  val kind = AttributeType.String[TrackElement]("kind")

  /**
    * Specifies the title of the text track
    */
  val label = AttributeType.String[TrackElement | OptionElement | OptGroupElement]("label")

  /**
    * Specifies the language of the element's content
    */
  val lang = AttributeType.String("lang")

  /**
    * Refers to a [DatalistElement] element that contains pre-defined options for an [InputElement] element
    */
  val list = AttributeType.String[InputElement]("list")

  /**
    * Specifies that the audio/video will start over again, every time it is finished
    */
  val loop = AttributeType.Boolean[AudioElement | VideoElement]("loop")

  /**
    * Specifies the range that is considered to be a low value
    */
  val low = AttributeType.Number/* [MeterElement] */("low") // TODO HTMLMeterElement

  /**
    * Specifies the maximum value
    */
  val max = AttributeType.Number[InputElement /* | MeterElement */ | ProgressElement]("max") // TODO HTMLMeterElement

  /**
    * Specifies the maximum number of characters allowed in an element
    */
  val maxLength = AttributeType.Number[InputElement | TextAreaElement]("maxlength")

  /**
    * Specifies what media/device the linked document is optimized for
     */
  val media = AttributeType.String[AnchorElement | AreaElement | LinkElement | SourceElement | StyleElement]("media")

  /**
    * Specifies the HTTP method to use when sending form-data
    */
  val method = AttributeType.String[FormElement]("method")

  /**
    * Specifies a minimum value
    */
  val min = AttributeType.Number[InputElement /* | MeterElement */]("min") // TODO HTMLMeterElement

  /**
    * Specifies that a user can enter more than one value
    */
  val multiple = AttributeType.Boolean[InputElement | SelectElement]("multiple")

  /**
    * Specifies that the audio output of the video should be muted
    */
  val muted = AttributeType.Boolean[VideoElement | AudioElement]("muted")

  /**
    * Specifies the name of the element
    */
  val name = AttributeType.String[ButtonElement | FieldSetElement | FormElement | IFrameElement | InputElement | MapElement | MetaElement | ObjectElement /* | OutputElement */ | ParamElement | SelectElement | TextAreaElement]("name") // TODO HTMLOutputElement

  /**
    * Specifies that the form should not be validated when submitted
    */
  val noValidate = AttributeType.Boolean[FormElement]("novalidate")

  /**
    * Script to be run on abort
    */
  val onAbort = AttributeType.Event[AudioElement | EmbedElement | ImageElement | ObjectElement | VideoElement, EventType.Event]("abort") // TODO use correct event type

  /**
    * Script to be run after the document is printed
    */
  val onAfterPrint = AttributeType.Event[BodyElement, EventType.Event]("afterprint") // TODO use correct event type

  /**
    * Script to be run before the document is printed
    */
  val onBeforePrint = AttributeType.Event[BodyElement, EventType.Event]("beforeprint") // TODO use correct event type

  /**
    * Script to be run when the document is about to be unloaded
    */
  val onBeforeUnload = AttributeType.Event[BodyElement, EventType.Event]("beforeunload") // TODO use correct event type

  /**
    * Script to be run when the element loses focus
    */
  val onBlur = AttributeType.Event[Element, Event]("blur") // TODO use correct event type

  /**
    * Script to be run when a file is ready to start playing (when it has buffered enough to begin)
    */
  val onCanPlay = AttributeType.Event[AudioElement | EmbedElement | ObjectElement | VideoElement, EventType.Event]("canplay") // TODO use correct event type

  /**
    * Script to be run when a file can be played all the way to the end without pausing for buffering
    */
  val onCanPlayThrough = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("canplaythrough") // TODO use correct event type

  /**
    * Script to be run when the value of the element is changed
    */
  val onChange = AttributeType.Event[Element, Event]("change") // TODO use correct event type

  /**
    * Script to be run when the element is being clicked
    */
  val onClick = AttributeType.Event[Element, Event]("click") // TODO use correct event type

  /**
    * Script to be run when a context menu is triggered
    */
  val onContextMenu = AttributeType.Event[Element, Event]("contextmenu") // TODO use correct event type

  /**
    * Script to be run when the content of the element is being copied
    */
  val onCopy = AttributeType.Event[Element, Event]("copy") // TODO use correct event type

  /**
    * Script to be run when the cue changes in a [TrackElement] element
    */
  val onCueChange = AttributeType.Event[TrackElement, EventType.Event]("cuechange") // TODO use correct event type

  /**
    * Script to be run when the content of the element is being cut
    */
  val onCut = AttributeType.Event[Element, Event]("cut") // TODO use correct event type

  /**
    * Script to be run when the element is being double-clicked
    */
  val onDoubleClick = AttributeType.Event[Element, Event]("dblclick") // TODO use correct event type

  /**
    * Script to be run when the element is being dragged
    */
  val onDrag = AttributeType.Event[Element, Event]("drag") // TODO use correct event type

  /**
    * Script to be run at the end of a drag operation
    */
  val onDragEnd = AttributeType.Event[Element, Event]("dragend") // TODO use correct event type

  /**
    * Script to be run when an element has been dragged to a valid drop target
    */
  val onDragEnter = AttributeType.Event[Element, Event]("dragenter") // TODO use correct event type

  /**
    * Script to be run when an element leaves a valid drop target
    */
  val onDragLeave = AttributeType.Event[Element, Event]("dragleave") // TODO use correct event type

  /**
    * Script to be run when an element is being dragged over a valid drop target
    */
  val onDragOver = AttributeType.Event[Element, Event]("dragover") // TODO use correct event type

  /**
    * Script to be run at the start of a drag operation
    */
  val onDragStart = AttributeType.Event[Element, Event]("dragstart") // TODO use correct event type

  /**
    * Script to be run when dragged element is being dropped
    */
  val onDrop = AttributeType.Event[Element, Event]("drop") // TODO use correct event type

  /**
    * Script to be run when the length of the media changes
    */
  val onDurationChange = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("durationchange") // TODO use correct event type

  /**
    * Script to be run when something bad happens and the file is suddenly unavailable (like unexpectedly disconnects)
    */
  val onEmptied = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("emptied") // TODO use correct event type

  /**
    * Script to be run when the media has reach the end (a useful event for messages like "thanks for listening")
    */
  val onEnded = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("ended") // TODO use correct event type

  /**
    * Script to be run when an error occurs
    */
  val onError = AttributeType.Event[AudioElement | BodyElement | EmbedElement | ImageElement | ObjectElement | ScriptElement | StyleElement | VideoElement, EventType.Event]("error") // TODO use correct event type

  /**
    * Script to be run when the element gets focus
    */
  val onFocus = AttributeType.Event[Element, Event]("focus") // TODO use correct event type

  /**
    * Script to be run when there has been changes to the anchor part of the a URL
    */
  val onHashChange = AttributeType.Event[BodyElement, EventType.Event]("hashchange") // TODO use correct event type

  /**
    * Script to be run when the element gets user input
    */
  val onInput = AttributeType.Event[Element, Event]("input") // TODO use correct event type

  /**
    * Script to be run when the element is invalid
    */
  val onInvalid = AttributeType.Event[Element, Event]("invalid") // TODO use correct event type

  /**
    * Script to be run when a user is pressing a key
    */
  val onKeyDown = AttributeType.Event[Element, Event]("keydown") // TODO use correct event type

  /**
    * Script to be run when a user presses a key
    */
  val onKeyPress = AttributeType.Event[Element, Event]("keypress") // TODO use correct event type

  /**
    * Script to be run when a user releases a key
    */
  val onKeyUp = AttributeType.Event[Element, Event]("keyup") // TODO use correct event type

  /**
    * Script to be run when the element is finished loading
    */
  val onLoad = AttributeType.Event[BodyElement | IFrameElement | ImageElement | InputElement | LinkElement | ScriptElement | StyleElement, EventType.Event]("load") // TODO use correct event type

  /**
    * Script to be run when media data is loaded
    */
  val onLoadedData = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("loadeddata") // TODO use correct event type

  /**
    * Script to be run when meta data (like dimensions and duration) are loaded
    */
  val onLoadedMetadata = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("loadedmetadata") // TODO use correct event type

  /**
    * Script to be run just as the file begins to load before anything is actually loaded
    */
  val onLoadStart = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("loadstart") // TODO use correct event type

  /**
    * Script to be run when a mouse button is pressed down on an element
    */
  val onMouseDown = AttributeType.Event[Element, Event]("mousedown") // TODO use correct event type

  /**
    * Script to be run as long as the  mouse pointer is moving over an element
    */
  val onMouseMove = AttributeType.Event[Element, Event]("mousemove") // TODO use correct event type

  /**
    * Script to be run when a mouse pointer moves out of an element
    */
  val onMouseOut = AttributeType.Event[Element, Event]("mouseout") // TODO use correct event type

  /**
    * Script to be run when a mouse pointer moves over an element
    */
  val onMouseOver = AttributeType.Event[Element, Event]("mouseover") // TODO use correct event type

  /**
    * Script to be run when a mouse button is released over an element
    */
  val onMouseUp = AttributeType.Event[Element, Event]("mouseup") // TODO use correct event type

  /**
    * Script to be run when a mouse wheel is being scrolled over an element
    */
  val onMouseWheel = AttributeType.Event[Element, Event]("mousewheel") // TODO use correct event type

  /**
    * Script to be run when the browser starts to work offline
    */
  val onOffline = AttributeType.Event[BodyElement, EventType.Event]("offline") // TODO use correct event type

  /**
    * Script to be run when the browser starts to work online
    */
  val onOnline = AttributeType.Event[BodyElement, EventType.Event]("online") // TODO use correct event type

  /**
    * Script to be run when a user navigates away from a page
    */
  val onPageHide = AttributeType.Event[BodyElement, EventType.Event]("pagehide") // TODO use correct event type

  /**
    * Script to be run when a user navigates to a page
    */
  val onPageShow = AttributeType.Event[BodyElement, EventType.Event]("pageshow") // TODO use correct event type

  /**
    * Script to be run when the user pastes some content in an element
    */
  val onPaste = AttributeType.Event[Element, Event]("paste") // TODO use correct event type

  /**
    * Script to be run when the media is paused either by the user or programmatically
    */
  val onPause = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("pause") // TODO use correct event type

  /**
    * Script to be run when the media has started playing
    */
  val onPlay = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("play") // TODO use correct event type

  /**
    * Script to be run when the media has started playing
    */
  val onPlaying = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("playing") // TODO use correct event type

  /**
    * Script to be run when the window's history changes.
    */
  val onPopState = AttributeType.Event[BodyElement, EventType.Event]("popstate") // TODO use correct event type

  /**
    * Script to be run when the browser is in the process of getting the media data
    */
  val onProgress = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("progress") // TODO use correct event type

  /**
    * Script to be run each time the playback rate changes (like when a user switches to a slow motion or fast forward mode).
    */
  val onRateChange = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("ratechange") // TODO use correct event type

  /**
    * Script to be run when a reset button in a form is clicked.
    */
  val onReset = AttributeType.Event[FormElement, EventType.Event]("reset") // TODO use correct event type

  /**
    * Script to be run when the browser window is being resized.
    */
  val onResize = AttributeType.Event[BodyElement, EventType.Event]("resize") // TODO use correct event type

  /**
    * Script to be run when an element's scrollbar is being scrolled
    */
  val onScroll = AttributeType.Event[Element, Event]("scroll") // TODO use correct event type

  /**
    * Script to be run when the user writes something in a search field (for [InputElement type="search"])
    */
  val onSearch = AttributeType.Event[InputElement, EventType.Event]("search") // TODO use correct event type

  /**
    * Script to be run when the seeking attribute is set to false indicating that seeking has ended
    */
  val onSeeked = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("seeked") // TODO use correct event type

  /**
    * Script to be run when the seeking attribute is set to true indicating that seeking is active
    */
  val onSeeking = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("seeking") // TODO use correct event type

  /**
    * Script to be run when the element gets selected
    */
  val onSelect = AttributeType.Event[Element, Event]("select") // TODO use correct event type

  /**
    * Script to be run when the browser is unable to fetch the media data for whatever reason
    */
  val onStalled = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("stalled") // TODO use correct event type

  /**
    * Script to be run when a Web Storage area is updated
    */
  val onStorage = AttributeType.Event[BodyElement, EventType.Event]("storage") // TODO use correct event type

  /**
    * Script to be run when a form is submitted
    */
  val onSubmit = AttributeType.Event[FormElement, EventType.Event]("submit") // TODO use correct event type

  /**
    * Script to be run when fetching the media data is stopped before it is completely loaded for whatever reason
    */
  val onSuspend = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("suspend") // TODO use correct event type

  /**
    * Script to be run when the playing position has changed (like when the user fast forwards to a different point in the media)
    */
  val onTimeUpdate = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("timeupdate") // TODO use correct event type

  /**
    * Script to be run when the user opens or closes the [DetailsElement] element
    */
  val onToggle = AttributeType.Event[/* [DetailsElement] */Element, Event]("toggle") // TOOD HTMLDetailsElement // TODO use correct event type

  /**
    * Script to be run when a page has unloaded (or the browser window has been closed)
    */
  val onUnload = AttributeType.Event[BodyElement, EventType.Event]("unload") // TODO use correct event type

  /**
    * Script to be run each time the volume of a video/audio has been changed
    */
  val onVolumeChange = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("volumechange") // TODO use correct event type

  /**
    * Script to be run when the media has paused but is expected to resume (like when the media pauses to buffer more data)
    */
  val onWaiting = AttributeType.Event[AudioElement | VideoElement, EventType.Event]("waiting") // TODO use correct event type

  /**
    * Script to be run when the mouse wheel rolls up or down over an element
    */
  val onWheel = AttributeType.Event[Element, Event]("wheel") // TODO use correct event type

  /**
    * Specifies that the details should be visible (open) to the user
    */
  val open = AttributeType.Boolean/* [DetailsElement] */("open") // TODO HTMLDetailsElement

  /**
    * Specifies what value is the optimal value for the gauge
    */
  val optimum = AttributeType.Number/* [MeterElement] */("optimum") // TODO HTMLMeterElement

  /**
    * Specifies a regular expression that an [InputElement] element's value is checked against
    */
  val pattern = AttributeType.String[InputElement]("pattern")

  /**
    * Specifies a short hint that describes the expected value of the element
    */
  val placeholder = AttributeType.String[InputElement | TextAreaElement]("placeholder")

  /**
    * Specifies a popover element
    */
  val popover = AttributeType.String("popover")

  /**
    * Specifies which popover element to invokde
    */
  val popoverTarget = AttributeType.String[ButtonElement | InputElement]("popovertarget")

  /**
    * Specifies what happens to the popover element when the button is clicked
    */
  val popoverTargetAction = AttributeType.String[ButtonElement | InputElement]("popovertargetaction") // TODO enum

  /**
    * Specifies an image to be shown while the video is downloading, or until the user hits the play button
    */
  val poster = AttributeType.String[VideoElement]("poster")

  /**
    * Specifies if and how the author thinks the audio/video should be loaded when the page loads
    */
  val preload = AttributeType.String[AudioElement | VideoElement]("preload") // TODO enum?

  /**
    * Specifies that the element is read-only
    */
  val readonly = AttributeType.Boolean[InputElement | TextAreaElement]("readonly")

  /**
    * Specifies the relationship between the current document and the linked document
    */
  val rel = AttributeType.String[AnchorElement | AreaElement | FormElement | LinkElement]("rel")

  /**
    * Specifies that the element must be filled out before submitting the form
    */
  val required = AttributeType.Boolean[InputElement | SelectElement | TextAreaElement]("required")

  /**
    * Specifies that the list order should be descending (9,8,7...)
    */
  val reversed = AttributeType.Boolean[OListElement]("reversed")

  /**
    * Specifies the visible number of lines in a text area
    */
  val rows = AttributeType.Number[TextAreaElement]("rows")

  /**
    * Specifies the number of rows a table cell should span
    */
  val rowSpan = AttributeType.Number[TableCellElement]("rowspan")

  /**
    * Enables an extra set of restrictions for the content in an [IFrameElement]
    */
  val sandbox = AttributeType.Boolean[IFrameElement]("sandbox")

  /**
    * Specifies whether a header cell is a header for a column, row, or group of columns or rows
    */
  val scope = AttributeType.String[TableCellElement]("scope") // TODO only th not td ???

  /**
    * Specifies that an option should be pre-selected when the page loads
    */
  val selected = AttributeType.Boolean[OptionElement]("selected")

  /**
    * Specifies the shape of the area
    */
  val shape = AttributeType.String[AreaElement]("shape")

  /**
    * Specifies the width, in characters (for [InputElement]) or specifies the number of visible options (for [SelectElement])
    */
  val size = AttributeType.Number[InputElement | SelectElement]("size")

  /**
    * Specifies the size of the linked resource
    */
  val sizes = AttributeType.String[ImageElement | LinkElement | SourceElement]("sizes") // TODO number x number

  /**
    * Specifies the number of columns to span
    */
  val span = AttributeType.Number[TableColElement]("span")

  /**
    * Specifies whether the element is to have its spelling and grammar checked or not
    */
  val spellcheck = AttributeType.Boolean("spellcheck")

  /**
    * Specifies the URL of the media file
    */
  val src = AttributeType.String[AudioElement | EmbedElement | IFrameElement | ImageElement | InputElement | ScriptElement | SourceElement | TrackElement | VideoElement]("src")

  /**
    * Specifies the HTML content of the page to show in the [IFrameElement]
    */
  val srcDoc = AttributeType.String[IFrameElement]("srcdoc")

  /**
    * Specifies the language of the track text data (required if kind="subtitles")
     */
  val srcLang = AttributeType.String[TrackElement]("srclang")

  /**
    * Specifies the URL of the image to use in different situations
    */
  val srcSet = AttributeType.String[ImageElement | SourceElement]("srcset")

  /**
    * Specifies the start value of an ordered list
    */
  val start = AttributeType.String[OListElement]("start")

  /**
    * Specifies the legal number intervals for an input field
    */
  val step = AttributeType.Number[InputElement]("step")

  /**
    * Specifies an inline CSS style for an element
    */
  val style = AttributeType.Style

  // object StyleAttribute extends Attribute[String, HTMLElement]:
  //   def set(element: HTMLElement)(value: String): Unit =
  //       element.style = value
  //   def remove(element: HTMLElement): Unit =
  //     element.style = null.asInstanceOf[String]
  //   // def := (styles: (CssCssValueWithValue | Signal[CssCssValueWithValue])*): Unit =
  //   //   styles.foreach:
  //   //     case style: CssCssValueWithValue =>
  //   //       set(element)(style)
  //   //     case signal: Signal[CssCssValueWithValue] =>
  //   //       val unsubscribe = signal.subscribe: newValue =>
  //   //         set(element)(newValue)
  //   //       () => unsubscribe()


  /**
    * Specifies the tabbing order of an element
    */
  val tabIndex = AttributeType.Number("tabindex")

  /**
    * Specifies the target for where to open the linked document or where to submit the form
    */
  val target = AttributeType.String[AnchorElement | AreaElement | BaseElement | FormElement]("target")

  /**
    * Specifies extra information about an element
    */
  val title = AttributeType.String("title")

  /**
    * Specifies whether the content of an element should be translated or not
    */
  val translate = AttributeType.Boolean("translate")

  /**
    * Specifies the type of element
    */
  val `type` = AttributeType.String[AnchorElement | ButtonElement | EmbedElement | InputElement | LinkElement | MenuElement | ObjectElement | ScriptElement | SourceElement | StyleElement]("type")

  /**
    * Specifies an image as a client-side image map
    */
  val usemap = AttributeType.Boolean[ImageElement | ObjectElement]("usemap")

  /**
    * Specifies the value of the element
    */
  val value = AttributeType.String[ButtonElement | InputElement | LIElement | OptionElement /* | MeterElement */ | ProgressElement | ParamElement]("value") // TODO HTMLMeterElement

  /**
    * Specifies the width of the element
    */
  val width = AttributeType.Number[CanvasElement | EmbedElement | IFrameElement | ImageElement | InputElement | ObjectElement | VideoElement]("width")

  /**
    * Specifies how the text in a text area is to be wrapped when submitted in a form
    */
  val wrap = AttributeType.String[TextAreaElement]("wrap")
