// Local-dev mirror of the Netlify proxy rewrite in src/wasmJsMain/resources/_redirects.
(function (config) {
  config.devServer = config.devServer || {};
  config.devServer.proxy = [
    {
      context: ["/api/connections"],
      target: "https://www.nytimes.com",
      changeOrigin: true,
      secure: true,
      pathRewrite: { "^/api/connections": "/svc/connections/v2" },
    },
  ];
})(config);
