# watch-accuracy
A simple tool to track how well a mechanical watch keeps time.

To take a reading use:
```bash
watch-solver 15:20:00
```

If the watch has had it's time adjusted manually you'll need to tell the app to reset it's rolling average
```bash
watch-solver 15:20:00 --reset-avg
```
