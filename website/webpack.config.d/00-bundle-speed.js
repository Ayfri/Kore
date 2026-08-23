// Kotlin/JS IR emits the whole app as one ~16 MiB module, which is the worst case for both of webpack's production
// defaults: `ModuleConcatenationPlugin` spends ~8 min scope-hoisting 17 siblings into it for ~1% of transfer size,
// and Terser then minifies the single resulting asset on one thread. SWC is native and multi-threaded within a file.
// Measured on the real bundle: 632s -> 16s, for +10 KB brotli. The IR compiler already inlines and DCEs beforehand.
;(function () {
	if (config.mode !== 'production') return;
	const TerserPlugin = require('terser-webpack-plugin');
	config.optimization = config.optimization || {};
	config.optimization.concatenateModules = false;
	config.optimization.minimizer = [
		new TerserPlugin({
			extractComments: false,
			minify: TerserPlugin.swcMinify,
			terserOptions: {
				compress: true,
				format: { comments: false },
				mangle: true,
			},
		}),
	];
})();
