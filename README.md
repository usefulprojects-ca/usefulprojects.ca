# usefulprojects.ca

## Deployment

## Development
### Dependencies

For development or running locally, ensure the following are available:
- [babashka](https://github.com/babashka/babashka#installation)
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Tasks

The [Babashka Task Runner](https://book.babashka.org/#tasks) is used for development tasks. 
Run `bb tasks` to see available tasks.

### Starting a development system

First start up the local dependencies by running `bb dev`.

You may start a REPL using `bb repl`, passing any additional profiles you wish
to use for your local tooling. The repl will start up with the `:dev` and 
`:test` project aliases enabled.

e.g.
```
bb repl eastwood reveal my-idiosyncratic-alias
```

Once started, use `(dev/restart)` to (re)start a development system.

Currently after modifying routes/handlers, re-evaluating 
`ca.usefulprojects.app/app` is required, but a full system restart is not needed.

To tear down development dependencies (e.g., to reset postgres if things get
into a bad state), run `bb rm-dev`.
