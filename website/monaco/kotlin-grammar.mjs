// Kotlin tokens for the playground editor, registered over Monaco's own grammar by `monaco.entry.mjs`: that one tags
// every lowercase name `identifier`, so calls never get a color. The token kinds follow IntelliJ's Kotlin highlighting
// as far as a lexer can tell them apart, `KoreMonacoTheme.kt` styles them like Material Darker. Brackets, comments and
// auto-closing pairs still come from Monaco's own language configuration.
export const kotlinLanguage = {
	defaultToken: '',
	tokenPostfix: '.kt',

	// Right before `(` or `{` these stay keywords (`if (`, `init {`), any other name is a call, so `data(...)` and
	// `set(...)` read as the DSL functions they are.
	controlKeywords: [
		'as', 'break', 'catch', 'class', 'constructor', 'context', 'continue', 'do', 'else', 'false', 'finally', 'for', 'fun',
		'if', 'in', 'init', 'interface', 'is', 'null', 'object', 'package', 'return', 'super', 'suspend', 'this', 'throw',
		'true', 'try', 'typealias', 'val', 'var', 'when', 'where', 'while',
	],

	// Annotation use-site targets (`field`, `file`, `param`...) are left out, they are mostly variable names, and `it`
	// is the implicit lambda parameter.
	keywords: [
		'abstract', 'actual', 'annotation', 'as', 'break', 'by', 'catch', 'class', 'companion', 'const', 'constructor',
		'context', 'continue', 'crossinline', 'data', 'do', 'dynamic', 'else', 'enum', 'expect', 'external', 'false', 'final',
		'finally', 'for', 'fun', 'get', 'if', 'import', 'in', 'infix', 'init', 'inline', 'inner', 'interface', 'internal', 'is',
		'lateinit', 'noinline', 'null', 'object', 'open', 'operator', 'out', 'override', 'package', 'private', 'protected',
		'public', 'reified', 'return', 'sealed', 'set', 'super', 'suspend', 'tailrec', 'this', 'throw', 'true', 'try',
		'typealias', 'val', 'var', 'vararg', 'when', 'where', 'while',
	],

	binaryDigits: /[01]+(_+[01]+)*/,
	digits: /\d+(_+\d+)*/,
	escapes: /\\(?:[bnrt'"\\$]|u[0-9a-fA-F]{4})/,
	hexDigits: /[0-9a-fA-F]+(_+[0-9a-fA-F]+)*/,
	// An operand then `name value` on the same line makes `name` an infix call (`"round" greaterThan 1`, `0 until n`),
	// unless it's a keyword (`x in xs`) or the branch of an `if` (`if (a) b else c`).
	infixAhead: /\s+[a-z_]\w*\s+(?!else\b)[\w"'(`!-]/,
	symbols: /[=><!~?:&|+\-*\/^%]+/,
	typeArguments: /(?:\s*<[\w\s,.?*:<>]*>)?/,

	tokenizer: {
		root: [
			{ include: '@whitespace' },
			[/(import|package)(\s+)(\w+)/, ['keyword', '', { token: 'identifier', next: '@path' }]],
			[/(fun)(\s+)/, ['keyword', { token: '', next: '@funName' }]],
			[/(class|interface|object|typealias)(\s+)(\w+)/, ['keyword', '', 'type.identifier']],
			[/(break|continue|return|super|this)(@@\w+)/, ['keyword', 'identifier']],
			[/get(?=\s*\(\s*\))|set(?=\s*\(\s*value\s*\))/, 'keyword'],

			[/[A-Z][A-Z\d_]+(?!\w)(?=@infixAhead)/, 'constant', '@infix'],
			[/[A-Z][A-Z\d_]+(?!\w)/, 'constant'],
			[/[A-Z]\w*(?=@typeArguments\s*\()/, 'function.constructor'],
			[/[A-Z]\w*(?=@infixAhead)/, 'type.identifier', '@infix'],
			[/[A-Z]\w*/, 'type.identifier'],

			[/`[^`]+`(?=@typeArguments\s*[({])/, 'function.call'],
			[/`[^`]+`/, 'identifier'],
			[/[a-z_]\w*(?=@typeArguments\s*[({])/, { cases: { '@controlKeywords': 'keyword', '@default': 'function.call' } }],
			[/[a-z_]\w*(?=@infixAhead)/, {
				cases: {
					'false|null|this|true': { token: 'keyword', next: '@infix' },
					'@keywords': 'keyword',
					it: { token: 'parameter.implicit', next: '@infix' },
					'@default': { token: 'identifier', next: '@infix' },
				},
			}],
			[/[a-z_]\w*/, { cases: { '@keywords': 'keyword', it: 'parameter.implicit', '@default': 'identifier' } }],
			[/@[a-zA-Z_]\w*/, 'annotation'],

			[/\(/, '@brackets', '@parens'],
			[/\{/, '@brackets', '@braces'],
			[/[)}[\]]/, '@brackets'],
			[/[<>](?!@symbols)/, '@brackets'],
			[/@symbols/, 'delimiter'],

			[/(@digits)[eE]([\-+]?(@digits))?[fF]?/, 'number.float'],
			[/(@digits)?\.(@digits)([eE][\-+]?(@digits))?[fF]?/, 'number.float'],
			[/0[xX](@hexDigits)[uU]?L?/, 'number.hex'],
			[/0[bB](@binaryDigits)[uU]?L?/, 'number.binary'],
			[/(@digits)[fF]/, 'number.float'],
			[/(@digits)[uU]?L?(?=@infixAhead)/, 'number', '@infix'],
			[/(@digits)[uU]?L?/, 'number'],
			[/[;,.]/, 'delimiter'],

			[/"([^"\\]|\\.)*$/, 'string.invalid'],
			[/"""/, 'string', '@rawString'],
			[/"/, 'string', '@string'],
			[/'[^\\']'/, 'string'],
			[/(')(@escapes)(')/, ['string', 'string.escape', 'string']],
			[/'/, 'string.invalid'],
		],

		braces: [
			[/\}/, '@brackets', '@pop'],
			{ include: '@root' },
		],

		parens: [
			[/\)(?=@infixAhead)/, { token: '@brackets', switchTo: '@infix' }],
			[/\)/, '@brackets', '@pop'],
			[/[a-z_]\w*(?=\s*=(?!=))/, 'parameter.named'],
			{ include: '@root' },
		],

		// Between `fun` and the declared name: type parameters and the receiver type.
		funName: [
			{ include: '@whitespace' },
			[/(?:[a-zA-Z_]\w*|`[^`]+`)(?=\s*\()/, 'function.declaration', '@pop'],
			[/[A-Z]\w*/, 'type.identifier'],
			[/[a-z_]\w*/, { cases: { '@keywords': 'keyword', '@default': 'identifier' } }],
			[/[<>]/, '@brackets'],
			[/[.,:?*]/, 'delimiter'],
			[/./, '@rematch', '@pop'],
		],

		// Only entered through `infixAhead`, so the next name is always on the same line.
		infix: [
			[/(\s+)([a-z_]\w*)/, ['', {
				cases: {
					'@keywords': { token: 'keyword', next: '@pop' },
					'@default': { token: 'function.call', next: '@pop' },
				},
			}]],
			[/./, '@rematch', '@pop'],
		],

		// The dotted path of an `import` or `package`, whose segments aren't keywords (`import ...utils.set`).
		path: [
			[/\.\*/, 'delimiter'],
			[/(\.)([A-Z][A-Z\d_]+)(?!\w)/, ['delimiter', 'constant']],
			[/(\.)([A-Z]\w*)/, ['delimiter', 'type.identifier']],
			[/(\.)(\w+)/, ['delimiter', 'identifier']],
			[/./, '@rematch', '@pop'],
		],

		string: [
			[/[^\\"$]+/, 'string'],
			[/@escapes/, 'string.escape'],
			[/\\./, 'string.escape.invalid'],
			{ include: '@templateEntry' },
			[/"(?=@infixAhead)/, { token: 'string', switchTo: '@infix' }],
			[/"/, 'string', '@pop'],
		],

		rawString: [
			[/[^"$]+/, 'string'],
			{ include: '@templateEntry' },
			[/"""/, 'string', '@pop'],
			[/"/, 'string'],
		],

		templateEntry: [
			[/\$\{/, 'string.escape', '@template'],
			[/(\$)([a-zA-Z_]\w*)/, ['string.escape', { cases: { it: 'parameter.implicit', this: 'keyword', '@default': 'identifier' } }]],
			[/\$/, 'string'],
		],

		template: [
			[/\}/, 'string.escape', '@pop'],
			{ include: '@root' },
		],

		whitespace: [
			[/[ \t\r\n]+/, ''],
			[/\/\*\*(?!\/)/, 'comment.doc', '@kdoc'],
			[/\/\*/, 'comment', '@comment'],
			[/\/\/.*$/, 'comment'],
		],

		comment: [
			[/[^/*]+/, 'comment'],
			[/\/\*/, 'comment', '@push'],
			[/\*\//, 'comment', '@pop'],
			[/[/*]/, 'comment'],
		],

		kdoc: [
			[/[^/*]+/, 'comment.doc'],
			[/\/\*/, 'comment.doc', '@push'],
			[/\*\//, 'comment.doc', '@pop'],
			[/[/*]/, 'comment.doc'],
		],
	},
};
