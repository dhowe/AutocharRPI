package autochar;

import java.util.Map;

public class CharUtils { // PORT from cutils.js
	
	String lang;
	Map<String, Word> wordCache;
	
	public CharUtils(Map charData,Map tradData, Map simpData, boolean loadDefs, String lang) {

	    this.lang = lang != "simplified" ? "traditional" : lang;


	    this.wordCache = {};

	    this.tradData = tradData;
	    this.simpData = simpData;

	    this.prefillCache(charData, tradData, loadDefs);
	    this.prefillCache(charData, simpData, loadDefs);
	    //console.log("simp",simpData.length, this.simpData.length);

	    // this.triggerData = {};
	    // let keys = Object.keys(TRIGGERS);
	    // for (var i = 0; i < keys.length; i++) {
	    //   let word = keys[i];
	    //   for (var j = 0; j < word.length; j++) {
	    //     if (!wordCache.hasOwnProperty(word[j])) {
	    //       console.log("No chardata for ";
	    //       continue;
	    //     }
	    //   }
	    //   if (charData.)
	    //   triggerData{}
	    // }

	    this.language(lang);
	    this.levenshtein = levenshtein;

	    if (!levenshtein) throw new Error("no levenshtein impl");

	    console.log("cUtils[" + Object.keys(charData).length + "," +
	      Object.keys(this.wordCache).length + "] " + this.lang);
	  }
	}

public void toggleLang() {
	    this.language(this.lang == "simplified" ? "traditional" : "simplified");
	 }
	
	public String language() {
		
	void language(type) { // get/set
	    if (type) {
	      this.lang = "traditional";
	      if (type == "simplified") {
	        if (this.simpData) {
	          this.lang = type;
	        } else {
	          console.warn("[WARN] No simplified data, call ignored!");
	        }
	      }
	      console.log("language: " + this.lang);
	    }
	    return type ? this : this.lang;
	  }

	public void prefillCache(chars, words, useDefinitions) {
	    if (words) {
	      let that = this;
	      Object.keys(words).forEach(function (lit) {
	        that.wordCache[lit] = that._createWord(lit, chars,
	          useDefinitions ? words[lit] : undefined);
	      });
	    }
	  }

	public int bestEditDistance(literal, words, hist, minAllowed) {

	    words = words || Object.keys(this.currentWords());
	    if (typeof minAllowed == "undefined") minAllowed = 1;

	    //console.log("bestEditDistance: "+literal);

	    let med, meds = [];
	    let bestMed = Number.MAX_SAFE_INTEGER;
	    let wes = this.getWord(literal).editString;

	    for (let i = 0; i < words.length; i++) {

	      // no dups and nothing in history, maintain length
	      if (literal == words[i] || words[i].length != literal.length ||
	        (typeof hist != "undefined" && hist && hist.contains(words[i]))) {
	        continue;
	      }

	      let wes2 = this.getWord(words[i]).editString;

	      let raw = this.levenshtein.get(literal, words[i]);
	      med = Math.max(0, raw) + this.levenshtein.get(wes, wes2);
	      //med = this.minEditDistance(literal, words[i]);

	      //console.log(i, words[i], med, "best="+bestMed);

	      if (med < minAllowed || med > bestMed) continue;

	      if (med < bestMed) bestMed = med;
	      if (!meds[med]) meds[med] = [];
	      meds[med].push(words[i]);
	    }

	    // return the best list
	    for (var i = 0; i < meds.length; i++) {
	      if (meds[i] && meds[i].length) return meds[i];
	    }

	    return []; // or nothing
	  };

	  minEditDistance(l1, l2) {
	    return this.levenshtein.get(this.getWord(l1).editString,
	      this.getWord(l2).editString) + Math.max(0, this.levenshtein.get(l1, l2) - 1);
	  }

	  public void cacheSize() {
	    return Object.keys(this.wordCache).length;
	  }

	  public Word _createWord(literal, charData, definition) {

	    let chars = [];
	    for (let i = 0; i < literal.length; i++) {
	      if (literal[i] != " ") {
	        if (!charData.hasOwnProperty(literal[i])) {
	          throw Error("_createWord() failed for " + literal[i] + " in "+literal);
	        }
	        chars.push(charData[literal[i]]);
	      } else {
	        chars.push([]);
	      }
	    }

	    return new Word(literal, chars, definition);
	  }

	  public Word getWord(literal, charData) {

	    if (this.wordCache && this.wordCache.hasOwnProperty(literal)) {
	      return this.wordCache[literal];
	    }

	    if (typeof charData == "undefined") {
	      if (!this.hasOwnProperty("charData")) {
	        throw Error("getWord: no charData for " + literal);
	      }
	      charData = this.charData;
	    }

	    console.log("[WARN] creating word object for " + litera);

	    var word = this._createWord(literal, charData);

	    if (this.wordCache) this.wordCache[literal] = word;

	    return word;
	  }

	  public String definition(literal) {
	    var words = this.currentWords();
	    return words.hasOwnProperty(literal) ? words[literal] : "---";
	  }

	  public Map currentWords() {
	    return this.lang == "simplified" ? this.simpData : this.tradData;
	  }

	  public Word randWord(length) {
	    if (typeof length == "undefined") throw Error("no length");

	    let word = null;
	    let words = this.currentWords();
	    while (!word || word.length != length) {
	      // keep going until we get the right length
	      word = this.getWord(this.randKey(words));
	    }
	    return word;
	  }

	  public Object randVal(o) {
	    let keys = Object.keys(o);
	    return keys[keys.length * Math.random() << 0];
	  }

	  public Object randKey(o) {
	    let keys = Object.keys(o);
	    return keys[keys.length * Math.random() << 0];
	  }

	  public String pad(str, len) {
	    while (str.length < len) str += "？";
	    return str;
	  }

	  public void renderPath(word, charIdx, renderer, scale, yoff, rgb) {

	    var chr = word.characters[charIdx]; // anything to draw?
	    if (chr.parts[0] < 0 && chr.parts[1] < 0) return;

	    if (!rgb || rgb.length != 3) rgb = BLACK;
	    if (typeof scale == "undefined") scale = 1;

	    var pg = renderer || this._renderer;
	    var ctx = pg.drawingContext;
	    ctx.fillStyle = "rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")";

	    for (var j = 0; j < chr.paths.length; j++) {
	      for (var i = 0; i < chr.paths[j].length; i++) {

	        var shift = renderer.width / 2;
	        ctx.translate(0, shift + yoff); // shift for mirror
	        if (charIdx > 0) ctx.translate(shift, 0); // shift for mirror
	        ctx.scale(.5, -.5); // mirror-vertically

	        if (chr.parts[j] >= i) {
	          ctx.scale(scale, scale);
	          ctx.fill(chr.paths[j][i]);
	        } // else console.log("skip", j, i);

	        /* // draw stroke
	        ctx.strokeStyle = "#777";
	        ctx.lineWidth = 6;
	        ctx.stroke(chr.paths[i]);
	        */

	        ctx.setTransform(1, 0, 0, 1, 0, 0); // reset transform
	      }
	    }
	  }
	}
}
