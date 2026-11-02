# DuckTape

## What is DuckTape?

DuckTape is a Scala 3 library for building web applications.

## Using DuckTape

Releases are published to [GitHub Packages](https://github.com/PhoenixmitX/DuckTape/packages) by the
*Build & Release* workflow (on every `v*` tag, or by dispatching it with *publish* enabled; bump
`ThisBuild / version` in `build.sbt` first, GitHub Packages rejects re-publishing an existing version).

GitHub Packages requires a token to download even public packages, so consumers need a resolver *and*
credentials. A classic token with `read:packages` works locally; in GitHub Actions `secrets.GITHUB_TOKEN` does.

```scala
ThisBuild / resolvers += "GitHub Packages (ducktape)" at "https://maven.pkg.github.com/phoenixmitx/ducktape"
ThisBuild / credentials ++= sys.env.get("GITHUB_TOKEN") // the realm must be exactly "GitHub Package Registry"
  .map(Credentials("GitHub Package Registry", "maven.pkg.github.com", "phoenixmitx", _)).toSeq

libraryDependencies += "net.apiduck" %%% "ducktape-core" % "0.0.6"
```
