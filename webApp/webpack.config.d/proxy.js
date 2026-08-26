// Mirror's Netlify's proxy rewrite behavior for dev.
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
    {
      context: ["/api/proxy"],
      target: "https://localhost",
      changeOrigin: true,
      secure: true,
      router: function (request) {
        const host = request.url.replace(/^\/api\/proxy\//, "").split("/")[0];
        return `https://${host}`;
      },
      pathRewrite: function (path) {
        return path.replace(/^\/api\/proxy\/[^/]+/, "");
      },
    },
  ];
})(config);
