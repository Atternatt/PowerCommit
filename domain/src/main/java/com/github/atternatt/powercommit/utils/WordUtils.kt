package com.github.atternatt.powercommit.utils

import jdk.xml.internal.SecuritySupport.getSystemProperty

fun String.wrap(wrapLength: Int, newLineStr: String? = null, wrapLongWords: Boolean = false): String? {
  var wrapLength = wrapLength
  var newLineStr = newLineStr
  if (newLineStr == null) {
    newLineStr = getSystemProperty("line.separator")
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