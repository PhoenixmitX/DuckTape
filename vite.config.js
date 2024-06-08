import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
  root: "modules/demo/src/main/vite",
  publicDir: "../public",
  resolve: {
    alias: { // TODO: https://github.com/rollup/plugins/tree/master/packages/alias#custom-resolvers
      "@": ".",
      "signal-polyfill/dist/wrapper": "signal-polyfill/dist/index.js",
    },
  },
  build: {
    assetsDir: "../assets",
    outDir: "../../../dist/",
    emptyOutDir: true,
    rollupOptions: {
      output: {
        entryFileNames: "assets/[name]-[hash].js",
        assetFileNames: "assets/[name]-[hash][extname]",
      },
      treeshake: {
        preset: "smallest",
        moduleSideEffects: "no-external",
      },
    },

  },





  // assetsInclude: [
  //   "src/main/vite/**/*",
  //   "src/main/resources/**/*",
  // ],
  plugins: [
    scalaJSPlugin({
      projectID: "demo"
    })
  ],

  // disable tree-shaking
  // optimizeDeps: {
  //   exclude: [
  //     "src/main/vite/**/*",
  //     "src/main/resources/**/*",
  //   ],
  // },
  // build: {
  //   rollupOptions: {
  //     external: /^resources\//g,
  //   }
  // },
  // assetsInclude: [
  //   "src/main/vite/**/*",
  // ],
});
