# godot-googleplay-games-services
Godot Android plugin for the Google Play Games Services

## Usage

### method
* signIn()

### signal
* _on_sign_in_success(player_id: String)
* _on_sign_in_failed

```gdscript
if Engine.has_singleton("GooglePlayGamesServices"):
		var singleton = Engine.get_singleton("GooglePlayGamesServices")
		singleton.connect("_on_sign_in_success", self, "_on_sign_in_success")
		singleton.connect("_on_sign_in_failed", self, "_on_sign_in_failed")
		singleton.signIn()

func _on_sign_in_success(player_id: String):
		print(player_id)

func _on_sign_in_failed():
		print("_on_sign_in_failed")
```


## Compiling

Steps to build:

1. Clone this Git repository
2. Run `./gradlew build` in the cloned repository
3. copy *.aar and gdap files
```bash
cp app/build/outputs/aar/GooglePlayGamesServices-release.aar ${YOUR_PROJECT}/android/plugin/
cp GooglePlayGamesServices.gdap ${YOUR_PROJECT}/android/plugin
```

