const path = require("path");

const config = {
  entry: "./com.enfore.n4js.n4idl.roundtrip.viewer/src-gen/index.js",
  context: __dirname,
  output: {
  	filename: "dist/bundle.js"
  },
  resolve: {
	alias: { 
		"com.enfore.n4js.n4idl.roundtrip.viewer": path.resolve(__dirname, "./com.enfore.n4js.n4idl.roundtrip.viewer"),
		"jointjs-bundle": path.resolve(__dirname, "./jointjs-bundle"),
		"@@cjs": path.resolve(__dirname, "./")
	}
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /(node_modules|bower_components)/,
        use: {
          loader: 'babel-loader',
        }
      }
    ]
  }
};

module.exports = config;
