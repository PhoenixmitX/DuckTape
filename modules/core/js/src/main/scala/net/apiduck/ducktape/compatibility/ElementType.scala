package net.apiduck.ducktape.compatibility

import org.scalajs.dom.*

object ElementType:

  type Element = HTMLElement

  // TODO these elements are missing in scala.js dom bindings
  type DataElement = HTMLElement // HTMLDataElement
  type DetailsElement = HTMLElement // HTMLDetailsElement
  type MeterElement = HTMLElement // HTMLMeterElement
  type OutputElement = HTMLElement // HTMLOutputElement
  type PictureElement = HTMLElement // HTMLPictureElement
  type TimeElement = HTMLElement // HTMLTimeElement

  type AnchorElement = HTMLAnchorElement
  type AreaElement = HTMLAreaElement
  type AudioElement = HTMLAudioElement
  type BaseElement = HTMLBaseElement
  type BodyElement = HTMLBodyElement
  type BRElement = HTMLBRElement
  type ButtonElement = HTMLButtonElement
  type CanvasElement = HTMLCanvasElement
  type DataListElement = HTMLDataListElement
  type DialogElement = HTMLDialogElement
  type DivElement = HTMLDivElement
  type DListElement = HTMLDListElement
  type EmbedElement = HTMLEmbedElement
  type FieldSetElement = HTMLFieldSetElement
  type FormElement = HTMLFormElement
  type HeadElement = HTMLHeadElement
  type HeadingElement = HTMLHeadingElement
  type HRElement = HTMLHRElement
  type HtmlElement = HTMLHtmlElement
  type IFrameElement = HTMLIFrameElement
  type ImageElement = HTMLImageElement
  type InputElement = HTMLInputElement
  type LabelElement = HTMLLabelElement
  type LegendElement = HTMLLegendElement
  type LIElement = HTMLLIElement
  type LinkElement = HTMLLinkElement
  type MapElement = HTMLMapElement
  type MenuElement = HTMLMenuElement
  type MetaElement = HTMLMetaElement
  type ModElement = HTMLModElement
  type ObjectElement = HTMLObjectElement
  type OListElement = HTMLOListElement
  type OptGroupElement = HTMLOptGroupElement
  type OptionElement = HTMLOptionElement
  type ParagraphElement = HTMLParagraphElement
  type ParamElement = HTMLParamElement
  type PreElement = HTMLPreElement
  type ProgressElement = HTMLProgressElement
  type QuoteElement = HTMLQuoteElement
  type ScriptElement = HTMLScriptElement
  type SelectElement = HTMLSelectElement
  type SourceElement = HTMLSourceElement
  type SpanElement = HTMLSpanElement
  type StyleElement = HTMLStyleElement
  type TableCaptionElement = HTMLTableCaptionElement
  type TableCellElement = HTMLTableCellElement
  type TableColElement = HTMLTableColElement
  type TableElement = HTMLTableElement
  type TableRowElement = HTMLTableRowElement
  type TableSectionElement = HTMLTableSectionElement
  type TemplateElement = HTMLTemplateElement
  type TextAreaElement = HTMLTextAreaElement
  type TitleElement = HTMLTitleElement
  type TrackElement = HTMLTrackElement
  type UListElement = HTMLUListElement
  type UnknownElement = HTMLUnknownElement
  type VideoElement = HTMLVideoElement
