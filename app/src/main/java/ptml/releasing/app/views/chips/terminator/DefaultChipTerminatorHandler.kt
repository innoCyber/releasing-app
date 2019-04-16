package ptml.releasing.app.views.chips.terminator

import ptml.releasing.app.views.chips.tokenizer.ChipTokenizer

import android.text.Editable


import java.util.HashMap

class DefaultChipTerminatorHandler : ChipTerminatorHandler {

    private var mChipTerminators: MutableMap<Char, Int>? = null
    private var mPasteBehavior = ChipTerminatorHandler.Companion.BEHAVIOR_CHIPIFY_TO_TERMINATOR

    override fun setChipTerminators(chipTerminators: Map<Char, Int>?) {
        mChipTerminators = chipTerminators as MutableMap<Char, Int>
    }

    override fun addChipTerminator(character: Char, behavior: Int) {
        if (mChipTerminators == null) {
            mChipTerminators = HashMap()
        }

        mChipTerminators!![character] = behavior
    }

    override fun setPasteBehavior(pasteBehavior: Int) {
        mPasteBehavior = pasteBehavior
    }

    override fun findAndHandleChipTerminators(tokenizer: ChipTokenizer, text: Editable, start: Int, end: Int, isPasteEvent: Boolean): Int {
        // If we don't have a tokenizer or any chip terminators, there's nothing to look for
        if (mChipTerminators == null) {
            return -1
        }

        val textIterator = TextIterator(text, start, end)
        var selectionIndex = -1

        characterLoop@ while (textIterator.hasNextCharacter()) {
            val theChar = textIterator.nextCharacter()
            if (isChipTerminator(theChar)) {
                val behavior = if (isPasteEvent && mPasteBehavior != ChipTerminatorHandler.PASTE_BEHAVIOR_USE_DEFAULT) mPasteBehavior else mChipTerminators!![theChar]
                var newSelection = -1
                when (behavior) {
                    ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL -> {
                        selectionIndex = handleChipifyAll(textIterator, tokenizer)
                        break@characterLoop
                    }
                    ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN -> newSelection = handleChipifyCurrentToken(textIterator, tokenizer)
                    ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR -> newSelection = handleChipifyToTerminator(textIterator, tokenizer)
                }

                if (newSelection != -1) {
                    selectionIndex = newSelection
                }
            }
        }

        return selectionIndex
    }

    private fun handleChipifyAll(textIterator: TextIterator, tokenizer: ChipTokenizer): Int {
        textIterator.deleteCharacter(true)
        tokenizer.terminateAllTokens(textIterator.text)
        return textIterator.totalLength()
    }

    private fun handleChipifyCurrentToken(textIterator: TextIterator, tokenizer: ChipTokenizer): Int {
        textIterator.deleteCharacter(true)
        val text = textIterator.text
        val index = textIterator.index
        val tokenStart = tokenizer.findTokenStart(text, index)
        val tokenEnd = tokenizer.findTokenEnd(text, index)
        if (tokenStart < tokenEnd) {
            val chippedText = tokenizer.terminateToken(text.subSequence(tokenStart, tokenEnd), null)
            textIterator.replace(tokenStart, tokenEnd, chippedText)
            return tokenStart + chippedText.length
        }
        return -1
    }

    private fun handleChipifyToTerminator(textIterator: TextIterator, tokenizer: ChipTokenizer): Int {
        val text = textIterator.text
        val index = textIterator.index
        if (index > 0) {
            val tokenStart = tokenizer.findTokenStart(text, index)
            if (tokenStart < index) {
                val chippedText = tokenizer.terminateToken(text.subSequence(tokenStart, index), null)
                textIterator.replace(tokenStart, index + 1, chippedText)
            } else {
                textIterator.deleteCharacter(false)
            }
        } else {
            textIterator.deleteCharacter(false)
        }
        return -1
    }

    private fun isChipTerminator(character: Char): Boolean {
        return mChipTerminators != null && mChipTerminators!!.keys.contains(character)
    }
}
