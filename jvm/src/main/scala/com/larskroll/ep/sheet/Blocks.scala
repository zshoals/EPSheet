/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Lars Kroll <bathtor@googlemail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */

package com.larskroll.ep.sheet

import com.larskroll.roll20.sheet._
import scalatags.Text.all._
import SheetImplicits._

object Blocks {
  def block(title: LabelsI18N, elems: SheetElement*): CoreBlock = CoreBlock(title, elems);
  def fblock(title: LabelsI18N, growStyle: scalatags.stylesheet.Cls, elems: SheetElement*): FlexBlock = FlexBlock(Some(title), growStyle, elems);
  def fblock(growStyle: scalatags.stylesheet.Cls, elems: SheetElement*): FlexBlock = FlexBlock(None, growStyle, elems);
  def frow(elems: SheetElement*): FlexRow = FlexRow(elems);
  def sblock(title: LabelsI18N, growStyle: scalatags.stylesheet.Cls, elems: SheetElement*): SmallBlock = SmallBlock(title, None, growStyle, elems);
  def sblock(title: LabelsI18N, titleRoll: Button, growStyle: scalatags.stylesheet.Cls, elems: SheetElement*): SmallBlock = SmallBlock(title, Some(titleRoll), growStyle, elems);
  def coreSeq(elems: SheetElement*) = GroupWithRenderer(CoreTabRenderer, elems);

  val flexFill = span(EPStyle.`flex-grow`, EPStyle.min1rem);
  //def roll(roll: Button, members: SheetElement*): RollContent = RollContent(roll, members);
}

case class CoreBlock(title: LabelsI18N, members: Seq[SheetElement]) extends FieldGroup {
  override def renderer = coreBlockRenderer;

  val coreBlockRenderer = new GroupRenderer {
    import GroupRenderer._

    override def fieldCombiner: FieldCombiner = { tags =>
      div(EPStyle.wrapBox,
        tags,
        div(EPStyle.wrapBoxTitle, title.attrs))
    };

    override def renderLabelled(l: LabelsI18N, e: Tag): Tag =
      div(EPStyle.labelGroup,
        e,
        div(EPStyle.subLabel, l.attrs));

    override def fieldRenderers: FieldRenderer = CoreTabRenderer.fieldRenderers;
  }
}

case class SmallBlock(title: LabelsI18N, titleRoll: Option[Button], growStyle: scalatags.stylesheet.Cls, members: Seq[SheetElement]) extends FieldGroup {
  override def renderer = coreBlockRenderer;

  val coreBlockRenderer = new GroupRenderer {
    import GroupRenderer._

    override def fieldCombiner: FieldCombiner = { tags =>
      val baseHeader = span(title.attrs);
      val header = titleRoll match {
        case Some(roll) => div(EPStyle.smallWrapBoxTitle, renderRoll(roll, baseHeader))
        case None       => div(EPStyle.smallWrapBoxTitle, baseHeader)
      }
      div(EPStyle.wrapBox, growStyle,
        tags,
        header)
    };

    override def renderLabelled(l: LabelsI18N, e: Tag): Tag =
      span(EPStyle.inlineLabelGroup,
        span(EPStyle.inlineLabel, l),
        e);

    override def renderDualModeWrapper(edit: Tag, pres: Tag): Tag = {
      div(display.inline, edit, pres);
    }

    override def fieldRenderers: FieldRenderer = CoreTabRenderer.fieldRenderers;
  }
}

case class FlexBlock(title: Option[LabelsI18N], growStyle: scalatags.stylesheet.Cls, members: Seq[SheetElement]) extends FieldGroup {
  override def renderer = flexBlockRenderer;

  val flexBlockRenderer = new GroupRenderer {
    import GroupRenderer._

    override def fieldCombiner: FieldCombiner = { tags =>
      title match {
        case Some(t) => div(EPStyle.wrapBox,
          div(EPStyle.`flex-container`, tags),
          div(EPStyle.wrapBoxTitle, t.attrs))
        case None => div(EPStyle.wrapBox,
          div(EPStyle.`flex-container`, tags))
      }

    };

    override def renderRoll(roll: Button, e: Tag): Tag = {
      button(`type` := "roll", name := roll.name, EPStyle.wrapButton, value := roll.roll.render,
        e)
    }

    override def renderLabelled(l: LabelsI18N, e: Tag): Tag =
      span(EPStyle.labelGroup, EPStyle.`flex-grow`, growStyle,
        e,
        div(EPStyle.subLabel, l.attrs));

    override def fieldRenderers: FieldRenderer = CoreTabRenderer.fieldRenderers;
  }
}

case class FlexRow(members: Seq[SheetElement]) extends FieldGroup {
  override def renderer = flexRowRenderer;

  val flexRowRenderer = new GroupRenderer {
    import GroupRenderer._

    override def fieldCombiner: FieldCombiner = { tags =>
      div(EPStyle.flexRow, EPStyle.`flex-container`, tags)
    };

    override def fieldRenderers: FieldRenderer = CoreTabRenderer.fieldRenderers;
  }
}

//case class RollContent(roll: Button, members: Seq[SheetElement]) extends FieldGroup {
//  override def renderer = rollRenderer;
//
//  val rollRenderer = new GroupRenderer {
//    import GroupRenderer._
//
//    override def fieldCombiner: FieldCombiner = { tags =>
//      button(`type` := "roll", name := roll.name, EPStyle.wrapButton, value := roll.roll.render,
//        tags)
//    };
//
//    override def fieldRenderers: FieldRenderer = CoreTabRenderer.fieldRenderers;
//  }
//}
