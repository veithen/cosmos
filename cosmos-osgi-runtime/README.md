# API

## Custom bundle manifest headers

### `Cosmos-AutoStart`

Indicates whether the bundle should be started automatically when the runtime is initialized. Only use this for bundles that are specifically designed to run on Cosmos and not expected to be deployed into other OSGi runtimes.

## Resources

### `META-INF/cosmos-autostart-bundles.list`

A list of bundles (specified by symbolic name) to start automatically when the runtime is initialized.
