package net.apiduck.ducktape.compatibility

object ElementType:

  // type Element = AnchorElement | AreaElement | AudioElement | BaseElement | BodyElement | BRElement | ButtonElement | CanvasElement | DataListElement | DialogElement | DivElement | DListElement | EmbedElement | FieldSetElement | FormElement | HeadElement | HeadingElement | HRElement | HtmlElement | IFrameElement | ImageElement | InputElement | LabelElement | LegendElement | LIElement | LinkElement | MapElement | MenuElement | MetaElement | ModElement | ObjectElement | OListElement | OptGroupElement | OptionElement | ParagraphElement | ParamElement | PreElement | ProgressElement | QuoteElement | ScriptElement | SelectElement | SourceElement | SpanElement | StyleElement | TableCaptionElement | TableCellElement | TableColElement | TableElement | TableRowElement | TableSectionElement | TemplateElement | TextAreaElement | TitleElement | TrackElement | UListElement | UnknownElement | VideoElement

  // // TODO these elements are missing in scala.js dom bindings.
  // type DataElement = Element // "Data"
  // type DetailsElement = Element // "Details"
  // type MeterElement = Element // "Meter"
  // type OutputElement = Element // "Output"
  // type PictureElement = Element // "Picture"
  // type TimeElement = Element // "Time"

  // type AnchorElement = "Anchor"
  // type AreaElement = "Area"
  // type AudioElement = "Audio"
  // type BaseElement = "Base"
  // type BodyElement = "Body"
  // type BRElement = "BR"
  // type ButtonElement = "Button"
  // type CanvasElement = "Canvas"
  // type DataListElement = "DataList"
  // type DialogElement = "Dialog"
  // type DivElement = "Div"
  // type DListElement = "DList"
  // type EmbedElement = "Embed"
  // type FieldSetElement = "FieldSet"
  // type FormElement = "Form"
  // type HeadElement = "Head"
  // type HeadingElement = "Heading"
  // type HRElement = "HR"
  // type HtmlElement = "Html"
  // type IFrameElement = "IFrame"
  // type ImageElement = "Image"
  // type InputElement = "Input"
  // type LabelElement = "Label"
  // type LegendElement = "Legend"
  // type LIElement = "LI"
  // type LinkElement = "Link"
  // type MapElement = "Map"
  // type MenuElement = "Menu"
  // type MetaElement = "Meta"
  // type ModElement = "Mod"
  // type ObjectElement = "Object"
  // type OListElement = "OList"
  // type OptGroupElement = "OptGroup"
  // type OptionElement = "Option"
  // type ParagraphElement = "Paragraph"
  // type ParamElement = "Param"
  // type PreElement = "Pre"
  // type ProgressElement = "Progress"
  // type QuoteElement = "Quote"
  // type ScriptElement = "Script"
  // type SelectElement = "Select"
  // type SourceElement = "Source"
  // type SpanElement = "Span"
  // type StyleElement = "Style"
  // type TableCaptionElement = "TableCaption"
  // type TableCellElement = "TableCell"
  // type TableColElement = "TableCol"
  // type TableElement = "Table"
  // type TableRowElement = "TableRow"
  // type TableSectionElement = "TableSection"
  // type TemplateElement = "Template"
  // type TextAreaElement = "TextArea"
  // type TitleElement = "Title"
  // type TrackElement = "Track"
  // type UListElement = "UList"
  // type UnknownElement = "Unknown"
  // type VideoElement = "Video"

  trait Element

  type DataElement = Element
  type DetailsElement = Element
  type MeterElement = Element
  type OutputElement = Element
  type PictureElement = Element
  type TimeElement = Element

  trait AnchorElement extends Element
  trait AreaElement extends Element
  trait AudioElement extends Element
  trait BaseElement extends Element
  trait BodyElement extends Element
  trait BRElement extends Element
  trait ButtonElement extends Element
  trait CanvasElement extends Element
  trait DataListElement extends Element
  trait DialogElement extends Element
  trait DivElement extends Element
  trait DListElement extends Element
  trait EmbedElement extends Element
  trait FieldSetElement extends Element
  trait FormElement extends Element
  trait HeadElement extends Element
  trait HeadingElement extends Element
  trait HRElement extends Element
  trait HtmlElement extends Element
  trait IFrameElement extends Element
  trait ImageElement extends Element
  trait InputElement extends Element
  trait LabelElement extends Element
  trait LegendElement extends Element
  trait LIElement extends Element
  trait LinkElement extends Element
  trait MapElement extends Element
  trait MenuElement extends Element
  trait MetaElement extends Element
  trait ModElement extends Element
  trait ObjectElement extends Element
  trait OListElement extends Element
  trait OptGroupElement extends Element
  trait OptionElement extends Element
  trait ParagraphElement extends Element
  trait ParamElement extends Element
  trait PreElement extends Element
  trait ProgressElement extends Element
  trait QuoteElement extends Element
  trait ScriptElement extends Element
  trait SelectElement extends Element
  trait SourceElement extends Element
  trait SpanElement extends Element
  trait StyleElement extends Element
  trait TableCaptionElement extends Element
  trait TableCellElement extends Element
  trait TableColElement extends Element
  trait TableElement extends Element
  trait TableRowElement extends Element
  trait TableSectionElement extends Element
  trait TemplateElement extends Element
  trait TextAreaElement extends Element
  trait TitleElement extends Element
  trait TrackElement extends Element
  trait UListElement extends Element
  trait UnknownElement extends Element
  trait VideoElement extends Element
