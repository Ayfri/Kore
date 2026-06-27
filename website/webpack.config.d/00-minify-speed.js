;(function () {
	if (config.mode !== 'production') return;
	const TerserPlugin = require('terser-webpack-plugin');
	config.optimization = config.optimization || {};
	config.optimization.minimizer = [
		new TerserPlugin({
			parallel: true,
			extractComments: false,
			terserOptions: {
				compress: { passes: 1, inline: 2 },
				mangle: true,
				format: { comments: false },
			},
		}),
	];
})();
