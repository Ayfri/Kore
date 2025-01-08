package io.github.ayfri.kore.website.utils

external class Object

fun obj(init: dynamic.() -> Unit): dynamic {
	return (Object()).apply(init)
}

fun initMCFunctionHighlighting() {
	val prism = js("window.Prism")

	val mcfunctionLanguage = obj {
		comment = obj {
			pattern = js("/^\\s*#.*$/m")
			greedy = true
		}

		command = obj {
			pattern = js("/^\\s*([a-z_]+)(?=\\s|$)/m")
			alias = "keyword"
		}

		resourceName = obj {
			pattern = js("/#?[a-z_][a-z0-9_\\.-]*:[a-z0-9_\\.\\/-]+/")
			alias = "function"
		}

		selector = obj {
			pattern = js("/@[apers](?:\\[([^\\]]+)\\])?/")
			alias = "class-name"
			inside = obj {
				boolean = js("/\\b(?:true|false|True|False)\\b/")
				operator = js("/[=!,]|\\.\\./")
				number = js("/[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?[bfdl]?\\b/")
				uuid = js("/\\b[0-9a-fA-F]+(?:-[0-9a-fA-F]+){4}\\b/")
				punctuation = js("/[\\[\\]]/")
				scores = obj {
					pattern = js("/scores=\\{[^}]*\\}/")
					inside = obj {
						punctuation = js("/[{}=,]/")
						property = js("/\\w+/")
						number = js("/[+-]?\\d*\\.?\\d+/")
						operator = js("/\\.\\.|[<>=]+/")
					}
				}
				`selector-tag` = obj {
					pattern = js("/\\b\\w+\\b/")
					alias = "keyword"
				}
				string = obj {
					pattern = js("/\"[^\"]*\"/")
					greedy = true
				}
			}
		}

		coordinate = obj {
			pattern = js("/[~^]|(?:[+-]?\\d*\\.?\\d+)/")
			alias = "number"
		}

		operator = obj {
			pattern = js("/[\\-%?!+*<>\\/|&=.:,;]/")
			alias = "operator"
		}

		property = obj {
			pattern = js("/#?[a-z_][a-z_\\.-]*:[a-z0-9_\\.-\\/]+(?=\\s*[=:])/")
			alias = "property"
		}

		nbtPath = obj {
			pattern = js("/[\\w\\-.]+(?:\\[[^\\]]*\\])*(?:\\.[\\w\\-.]+(?:\\[[^\\]]*\\])*)*/")
			inside = obj {
				property = js("/[\\w\\-.]+/")
				string = obj {
					pattern = js("/\"[^\"]*\"/")
					greedy = true
				}
				number = js("/[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?[bfdl]?\\b/")
				punctuation = js("/[.\\[\\]]/")
			}
		}

		string = obj {
			pattern = js("/\"(?:\\\\.|[^\\\\\"\\r\\n])*\"/")
			greedy = true
		}

		json = obj {
			pattern = js("/\\{(?:[^{}]|\\{(?:[^{}]|\\{[^{}]*\\})*\\})*\\}/")
			inside = obj {
				property = obj {
					pattern = js("/\"[^\"]*\"(?=\\s*:)/")
					greedy = true
				}
				string = obj {
					pattern = js("/\"(?:\\\\.|[^\\\\\"\\r\\n])*\"/")
					greedy = true
				}
				number = js("/[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?\\b/")
				punctuation = js("/[{}[\\],]/")
				operator = js("/:/")
				boolean = js("/\\b(?:true|false)\\b/")
				`null` = obj {
					pattern = js("/\\bnull\\b/")
					alias = "keyword"
				}
			}
		}

		nbt = obj {
			pattern = js("/\\{[^}]*\\}/")
			inside = obj {
				property = js("/[\\w\\-.]+/")
				punctuation = js("/[{},:]/")
				string = obj {
					pattern = js("/\"[^\"]*\"/")
					greedy = true
				}
				number = js("/[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?[bfdl]?\\b/")
				boolean = js("/\\b(?:true|false|True|False)\\b/")
				array = obj {
					pattern = js("/\\[[^\\]]*\\]/")
					inside = obj {
						punctuation = js("/[\\[\\],]/")
						number = js("/[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?[bfdl]?\\b/")
						string = obj {
							pattern = js("/\"[^\"]*\"/")
							greedy = true
						}
					}
				}
			}
		}

		punctuation = js("/[\\[\\](){}:,]/")
	}

	prism.languages.mcfunction = mcfunctionLanguage
}
