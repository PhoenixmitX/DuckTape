import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
  // "/" locally, "/DuckTape/" when the Pages workflow builds (see .github/workflows/deploy-pages.yml)
  base: process.env.BASE_PATH || "/",
  root: "modules/demo/src/main/vite",
  publicDir: "../public",
  build: {
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
  plugins: [
    scalaJSPlugin({
      projectID: "demo"
    })
  ],
});
