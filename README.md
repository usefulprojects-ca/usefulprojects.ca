# usefulprojects.ca

## What it is

This code serves two purposes:

- Provide the site powering usefulprojects.ca
- Provide an environment for testing technologies and approaches for building
Clojure based web services.

Due to the latter, code that may be overkill for the former is welcome. 

## Configuration
### Configuration

Configuration is handled using [aero](https://github.com/juxt/aero).

There are three configuration files, stored in `/config`. 
Only `config.edn` is checked into source control; 
example templates are provided for the others.

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

A best effort is made to prevent values provided in this file from being leaked 
in e.g. log files or error messages.

An example is provided, see `config/secrets.example.edn`.

#### environment.edn

Contains any environment specific overrides needed.
This file is _not_ source controlled.

An example is provided, see `config/environment.example.edn`.

## Development
### Dependencies

For local development, ensure the following are installed on your system:
- [babashka](https://github.com/babashka/babashka#installation)
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [tailwindcss](https://github.com/tailwindlabs/tailwindcss/releases/latest)

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

The `:local` configuration profile used by the REPL system will configure the 
system such that individual page handlers will be picked up upon being evaluated.
No full system reset is needed. New development should support this workflow.

#### Tailwind Compilation

Run `(dev/start-tailwind-compile-watcher)` to start a file watcher that will
recompile the tailwind css upon any file changes.

The tailwindcss standalone compiler (which you must install yourself) comes with
the official tailwind plugins included.

Tailwind will scan all source files for mentions of tailwind classes. This means
that classes _must_ be written explicitly. Classes that are constructed
dynamically will not be picked up and will not end up in the compiled css.

#### Code style

TODO Write code style docs and explore automated linting options

## Deployment

TODO Setup deployment

## Decision Record

TODO Flesh this section out

### Stateful component handling: Integrant

### Routing: Reitit

### Configuration: Aero

### Primary datastore: XTDB

### Frontend: HTMX / Hyperscript

### Data validation: Malli

### CSS Framework: Tailwind
