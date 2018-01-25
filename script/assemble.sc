#!/usr/bin/env amm

import java.io.File;
import ammonite.ops._
import ammonite.ops.ImplicitWd._

val jsfileFast = pwd / RelPath("target/scala-2.12/ep-api-script-fastopt.js");
val jsfileFull = pwd / RelPath("target/scala-2.12/ep-api-script-opt.js");
val deployfile = pwd / RelPath("ep-script.js");
val rootscript = "com.lkroll.ep.api.EPScripts";

@main
def main(version: String, full: Boolean = false): Unit = {
	if (full) {
		assemble(version, jsfileFull);
	} else {
		assemble(version, jsfileFast);
	}
}

def assemble(version: String, jsfile: Path) {
	try {
		println(s"Assembling v${version}...");
		if (exists(deployfile)) {
			rm(deployfile);
		}
		val jsraw = read(jsfile);
		val jsfull = jsraw ++ "\n" ++ s"${rootscript}().load();";
		write(deployfile, jsfull);
		println("Done!");
	} catch {
		case e: Throwable => e.printStackTrace(Console.err);
	}
}