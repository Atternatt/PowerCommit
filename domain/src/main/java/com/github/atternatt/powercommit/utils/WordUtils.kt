/*
 * MIT License
 *
 * Copyright (c) 2022 Marc Moreno Ferrer
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
 */

package com.github.atternatt.powercommit.utils

fun String.wrap(wrapLength: Int, newLineStr: String? = null, wrapLongWords: Boolean = false): String? {
  var wrapLength = wrapLength
  var newLineStr = newLineStr
  if (newLineStr == null) {
    newLineStr = System.lineSeparator()
  }
  if (wrapLength < 1) {
    wrapLength = 1
  }
  val inputLineLength = length
  var offset = 0
  val wrappedLine = StringBuilder(inputLineLength + 32)
  while (inputLineLength - offset > wrapLength) {
    if (this[offset] == ' ') {
      offset++
      continue
    }
    var spaceToWrapAt = lastIndexOf(' ', wrapLength + offset)
    if (spaceToWrapAt >= offset) {
      // normal case
      wrappedLine.append(substring(offset, spaceToWrapAt))
      wrappedLine.append(newLineStr)
      offset = spaceToWrapAt + 1
    } else {
      // really long word or URL
      if (wrapLongWords) {
        // wrap really long word one line at a time
        wrappedLine.append(substring(offset, wrapLength + offset))
        wrappedLine.append(newLineStr)
        offset += wrapLength
      } else {
        // do not wrap really long word, just extend beyond limit
        spaceToWrapAt = indexOf(' ', wrapLength + offset)
        offset = if (spaceToWrapAt >= 0) {
          wrappedLine.append(substring(offset, spaceToWrapAt))
          wrappedLine.append(newLineStr)
          spaceToWrapAt + 1
        } else {
          wrappedLine.append(substring(offset))
          inputLineLength
        }
      }
    }
  }

  // Whatever is left in line is short enough to just pass through
  wrappedLine.append(substring(offset))
  return wrappedLine.toString()
}