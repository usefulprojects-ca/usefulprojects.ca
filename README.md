# usefulprojects.ca

## What it is

This code serves two purposes:

- Provide the site powering usefulprojects.ca
- Provide an environment for testing technologies and exercising Clojure 
development.

Due to the second purpose, the code is considerable overkill for the first 
purpose.


## Configuration
### Configuration

Configuration is handled using [aero](https://github.com/juxt/aero).

Configuration files are stored in the `/config` directory and span three files.

Only `config.edn` is checked into source control; example templates are provided
for the others.

#### config.edn

Main entry point for configuration, read by `aero` when the system is built.

There are four profiles that may be used:

- `:local` for the system started at the local repl
- `:test` for the system used for local testing
- `:ci` for the system used by CI testing
- `:prod` for the deployed system

This file is source controlled.

#### secrets.edn

Contains any sensitive configuration values. 
This file is _not_ source controlled.

An example is provided, see `config/secrets.example.edn`.

#### overrides.edn

Contains any host specific overrides needed.
This file is _not_ source controlled.

An example is provided, see `config/overrides.example.edn`.

## Development
### Dependencies

For development or running locally, ensure the following are available:
- [babashka](https://github.com/babashka/babashka#installation)
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Tasks

The [Babashka Task Runner](https://book.babashka.org/#tasks) is used for 
development tasks. 

Run `bb tasks` to see a list of available tasks, `bb {task}` to run the task.

Most tasks are fairly trivial wrappers over a shell command or two; they are 
wrapped up here to help the developer avoid cluttering their mind attic with
remembering specifics.

### Starting a development system
#### Configuration

You may run `bb init-config` to create a full set of configuration files using
the provided examples, which contain suitable defaults for local development.

#### Service dependencies

Start up the local service dependencies by running `bb dev`, which will shell
out to `docker-compose`.

To tear down development dependencies to start from scratch, (e.g., to reset 
postgres if things get into a bad state), run `bb rm-dev`.

#### REPL

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


## Deployment

Coming soon!
