package autochar;

public class Word {

	public Word(String literal, chars, definition) {

	    this.literal = literal;
	    this.characters = chars;
	    this.length = literal.length;
	    this.definition = definition;
	    this.editString = this.computeEditString();
	    this.characters.forEach(this.computeParts); // 2-parts-per-char
	    this.characters.forEach(this.computeStrokes); // strokes-per-path
	    this.characters.forEach(this.computePaths); // path2Ds-per-stroke
	  }

	  public void computeParts(chr) {
	    // assume 2 parts per char, otherwise check decomposition
	    chr.parts = new Array(2);
	    chr.parts.fill(Number.MAX_SAFE_INTEGER);
	  }

	  // divide strokes into character parts
	  public void computeStrokes(chr) {

	    // a char has ~2 parts, each with a list of strokes
	    chr.cstrokes = [];
	    for (var i = 0; i < chr.parts.length; i++) {
	      chr.cstrokes[i] = [];
	    }

	    for (var j = 0; j < chr.matches.length; j++) {
	      var strokeIdx = chr.matches[j][0];
	      if (strokeIdx === 0) { // part 0
	        chr.cstrokes[0].push(chr.strokes[j]);
	      } else if (strokeIdx === 1) { // part 1
	        chr.cstrokes[1].push(chr.strokes[j]);
	      } else { // should never happen
	        console.error("Null stroke match at [" + j + "]0");
	      }
	    }
	  }

	  public void computePaths(chr) { // TODO: make sure this happens only once per char

	    chr.paths = [];
	    for (var i = 0; i < chr.parts.length; i++) chr.paths[i] = [];

	    for (var j = 0; j < chr.parts.length; j++) {
	      for (var i = 0; i < chr.cstrokes[j].length; i++) {
	        chr.paths[j].push(new Path2D(chr.cstrokes[j][i]));
	      }
	    }
	  }

	  public void computeEditString() {
	    var es = "";
	    for (var i = 0; i < this.characters.length; i++) {
	      es += this.characters[i].decomposition;
	      if (i < this.characters.length - 1) es += " ";
	    }
	    return es;
	  }

	  public void eraseStroke(charIdx, partIdx) { // returns true if changed

	    if (typeof charIdx === "undefined") throw new Error("no charIdx");
	    if (typeof partIdx === "undefined") throw new Error("no partIdx");

	    var chr = this.characters[charIdx];
	    partIdx = this.constrain(partIdx, 0, chr.parts.length - 1);

	    if (partIdx < 0 || partIdx >= chr.parts.length) {
	      throw new Error("bad partIdx: " + partIdx);
	    }

	    chr.parts[partIdx] = Math.min(chr.parts[partIdx], chr.cstrokes[partIdx].length - 1);

	    if (--chr.parts[partIdx] >= -1) {
	      //console.log("eraseStroke:char[" + charIdx + "][" + partIdx + "] = " +
	      //(chr.parts[partIdx]) + "/" + (chr.cstrokes[partIdx].length)); // keep
	      return true;
	    }
	    return false;
	  }

	  public void nextStroke(charIdx, partIdx) { // returns true if changed

	    if (typeof charIdx === "undefined") throw new Error("no charIdx");
	    if (typeof partIdx === "undefined") throw new Error("no partIdx");

	    charIdx = Math.max(charIdx, 0); // if -1, show first char
	    partIdx = Math.max(partIdx, 0); // if -1, show first part

	    var chr = this.characters[charIdx];

	    //console.log("char["+ charIdx+"]["+partIdx+"] = " +
	    //(chr.parts[partIdx]+1)+"/"+(chr.cstrokes[partIdx].length)); // keep

	    return (++this.characters[charIdx].parts[partIdx] <
	      this.characters[charIdx].cstrokes[partIdx].length - 1);
	  }

	  public static void constrain(n, low, high) { return Math.max(Math.min(n, high), low); }

	  ///////////////////////// visibility (redo) ///////////////////////////////

	  public boolean isVisible() { // true if word is fully drawn
	    for (var i = 0; i < this.characters.length; i++) {
	      if (!this.isCharVisible(i)) return false;
	    }
	    return true;
	  }

	  public boolean isHidden() { // true if all strokes are hidden
	    for (var i = 0; i < this.characters.length; i++) {
	      if (!this.isCharHidden(i)) return false;
	    }
	    return true;
	  }

	  public boolean isCharVisible(charIdx) { // true if character is fully drawn
	    var chr = this.characters[charIdx];
	    if (!chr) throw new Error("no charIdx for: " + charIdx);
	    for (var i = 0; i < chr.parts.length; i++) {
	      if (!this.isPartVisible(charIdx, i))
	        return false;
	    }
	    return true;
	  }

	  public boolean isCharHidden(charIdx) { // true if character is fully drawn
	    var chr = this.characters[charIdx];
	    if (!chr) throw new Error("no charIdx for: " + charIdx);
	    for (var i = 0; i < chr.parts.length; i++) {
	      if (!this.isPartHidden(charIdx, i))
	        return false;
	    }
	    return true;
	  }

	  public boolean isPartVisible(charIdx, partIdx) { // true if part is fully drawn
	    if (typeof charIdx === "undefined") throw new Error("no charIdx");
	    if (typeof partIdx === "undefined") throw new Error("no partIdx");
	    var chr = this.characters[charIdx];
	    //console.log("check "+chr.parts[partIdx]+ " >=? "+(chr.cstrokes[partIdx].length-1));
	    return (chr.parts[partIdx] >= chr.cstrokes[partIdx].length - 1);
	  }

	  public boolean isPartHidden(charIdx, partIdx) { // true if part is fully drawn
	    if (typeof charIdx === "undefined") throw new Error("no charIdx");
	    if (typeof partIdx === "undefined") throw new Error("no partIdx");
	    var chr = this.characters[charIdx];
	    //console.log("check "+chr.parts[partIdx]+ " >=? "+(chr.cstrokes[partIdx].length-1));
	    return (chr.parts[partIdx] < 0);
	  }

	  public void show(charIdx, partIdx) {
	    var ALL = Number.MAX_SAFE_INTEGER;
	    if (typeof charIdx === "undefined") {
	      this.setVisible(0, ALL); // show both chars
	      this.setVisible(1, ALL);
	    } else {
	      var chr = this.characters[charIdx];
	      if (!chr) throw new Error("show: no charIdx for: " + charIdx);
	      if (typeof partIdx === "undefined") {
	        this.setVisible(charIdx, ALL); // show one char
	      } else {
	        this.characters[charIdx].parts[partIdx] = ALL; // show one part
	      }
	    }
	  }

	  public void hide(charIdx, partIdx) {
	    if (typeof charIdx === "undefined") {
	      for (var i = 0; i < this.characters.length; i++) {
	        this.setVisible(i, -1); // hide all chars
	      }

	    } else {

	      if (!chr) throw new Error("hide: no charIdx for: " + charIdx);
	      if (typeof partIdx === "undefined") {
	        this.setVisible(charIdx, -1); // hide one char
	      } else {
	        this.characters[charIdx].parts[partIdx] = -1; // hide one part
	      }
	    }
	  }

	  public void setVisible(charIdx, value) { // -1(none), 0(left), 1(right), max(both)

	    if (arguments.length != 2) throw new Error("bad args: " + arguments.length);

	    if (typeof charIdx === "undefined") throw new Error("no charIdx");

	    var ALL = Number.MAX_SAFE_INTEGER;

	    var chr = this.characters[charIdx];
	    //console.log("setVisible", charIdx, value);
	    for (var i = 0; i < chr.parts.length; i++) chr.parts[i] = ALL;

	    if (value == 0) { // show left-only
	      if (chr.parts.length > 0) chr.parts[1] = -1;

	    } else if (value == 1) { // show right-only
	      chr.parts[0] = -1;

	    } else if (value < 0) { // show none
	      chr.parts[0] = -1;
	      chr.parts[1] = -1;

	    } else if (value != ALL) {
	      throw new Error("setVisible() got bad value: " + value);
	    }
	  }
	}
}
