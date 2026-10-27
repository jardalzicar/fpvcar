# FPVCar

FPV (first-person view) RC car built around a Raspberry Pi. The Pi streams live H.264 video from its camera to a ground station computer, and the ground station sends steering and throttle commands back over the network. An Arduino on the car turns those commands into PWM signals for the steering servo and the ESC.

## How it works

```
  GROUND STATION (computer)                         VEHICLE
 ┌───────────────────────┐                ┌───────────────────────┐
 │  carcontrol           │  UDP :8888     │  Raspberry Pi         │
 │  (JavaFX app)         │ ─────────────> │  udp.py               │
 │                       │  steering +    │                       │
 │                       │  throttle      │                       │
 │                       │  50 Hz         │                       │
 │                       │                │                       │
 │  stream.sh            │  UDP :9000     │  raspivid +           │
 │  (GStreamer viewer)   │ <───────────── │  GStreamer            │
 │                       │  RTP / H.264   │                       │
 │                       │  1280x720 25fps│                       │
 └───────────────────────┘                └───────────┬───────────┘
                                                      │ USB serial
                                                      │ 115200 baud
                                                      v
                                          ┌───────────────────────┐
                                          │  Arduino (auto2.ino)  │
                                          └─────┬───────────┬─────┘
                                     PWM pin 3  │           │  PWM pin 6
                                                v           v
                                       ┌──────────────┐ ┌──────────────┐
                                       │ Steering     │ │ ESC / motor  │
                                       │ servo        │ │              │
                                       └──────────────┘ └──────────────┘
```

1. **Video**: `raspivid` captures H.264 from the Pi camera. GStreamer packs it into RTP and sends it over UDP to the ground station, where `stream.sh` decodes and displays it.
2. **Control**: the JavaFX app reads the arrow keys and sends the current steering and throttle values over UDP 50 times per second.
3. **Relay**: `udp.py` on the Pi passes each UDP packet unchanged to the Arduino over USB serial.
4. **Actuation**: the Arduino parses the values and writes them to the servo and ESC using the `Servo` library.
5. **Failsafe**: if the Arduino gets no non-neutral command for about 100 ms, it returns steering and throttle to neutral, so the car stops when the link drops.

## Repository layout

```
.
├── vehicle/
│   ├── raspberry pi/
│   │   ├── setup.sh          # starts the control relay and the video stream
│   │   └── udp.py            # UDP → serial relay
│   └── arduino/
│       └── auto2/
│           └── auto2.ino     # servo + ESC control with failsafe
└── ground station/
    ├── carcontrol/           # JavaFX ground station app (IntelliJ project)
    │   ├── src/carcontrol/
    │   └── lib/stream.sh     # GStreamer video receiver
    └── analog_reading/
        └── analog_reading.ino  # physical joystick prototype (work in progress)
```

## Hardware

**Vehicle**
- RC car with a standard PWM steering servo and ESC
- Raspberry Pi with a Pi Camera module and Wi-Fi
- Arduino (any board with a USB serial port, such as an Uno), connected to the Pi over USB
- Servo signal on Arduino **pin 3**, ESC signal on **pin 6**, and a common ground between the Arduino and the ESC/servo

**Ground station**
- A computer on the same network as the Pi (developed on macOS)
- Optional: an Arduino with potentiometers for the physical joystick prototype

## Setup

### 1. Arduino (vehicle)

Open `vehicle/arduino/auto2/auto2.ino` in the Arduino IDE and upload it to the board. Neutral for both channels is `90`. Change `zeroThrottle` and `zeroStearing` if your servo or ESC needs trimming.

### 2. Raspberry Pi

Install the dependencies:

```bash
sudo apt install python-serial gstreamer1.0-tools gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

Then:

- Enable the camera in `raspi-config`. `setup.sh` uses `raspivid`, which needs the legacy camera stack.
- Copy `setup.sh` to the Pi and put `udp.py` in a `scripts/` folder next to it. `setup.sh` runs it as `scripts/udp.py`.
- In `setup.sh`, set the `udpsink host=` value to your ground station's hostname or IP. The default is `jarda-mac.local`.
- Check that the Arduino shows up as `/dev/ttyACM0`. If not, change `SERIAL` in `udp.py`.
- `udp.py` listens on `raspberrypi.local:8888`. Change `IP` if your Pi has a different hostname.

Start both the video stream and the control relay:

```bash
./setup.sh
```

### 3. Ground station

Install GStreamer with the H.264 decoder (`avdec_h264` comes from the libav plugin):

```bash
brew install gstreamer
```

Build and run the JavaFX app:

- Open `ground station/carcontrol` in IntelliJ IDEA.
- Use a JDK that includes JavaFX (for example JDK 8, or a newer JDK with OpenJFX added).
- Run `carcontrol.Main` with the **working directory set to `ground station/carcontrol`**, so the app can find `lib/stream.sh`.
- The app sends control packets to `raspberrypi.local:8888`. Change this in `Main.java` if needed.

You can also run the video viewer on its own:

```bash
./lib/stream.sh
```

## Usage

1. Power the car and start `setup.sh` on the Pi.
2. Start the ground station app.
3. Click **Video stream** to open the camera feed.
4. Click **Remote control** to start sending commands. The sliders show the current steering and throttle.
5. Drive with the arrow keys:

| Key | Action |
| --- | --- |
| ↑ / ↓ | Throttle forward / reverse. Holding the key ramps up faster. Releasing it returns to neutral. |
| ← / → | Steer. Releasing the key centres the wheels. |

The **Joystick** button listens on local UDP port `8000` for input from the physical joystick prototype. It is not finished yet.

## Control protocol

Each command is one ASCII line sent as a UDP datagram to the Pi and passed unchanged to the Arduino:

```
<steering> <throttle>\n
```

- Both values are servo angles shifted by **+1**, so they range from `1` to `181`, and neutral is `91`. The offset lets the Arduino tell a real `0°` apart from the `0` that `Serial.parseInt()` returns on timeout.
- The Arduino ignores values outside `1–181`.
- The Arduino echoes the values it applied back over serial (`stearing: 90 throttle: 90`), and `udp.py` prints that echo.

## Known limitations

- **Local network only by default.** Hostnames are resolved over mDNS (`*.local`). To drive over the internet, use a VPN such as WireGuard or Tailscale rather than forwarding ports.
- **No authentication on the control channel.** Anyone who can reach UDP port 8888 on the Pi can send commands.
- **Older software stack.** `udp.py` is written for Python 2, `raspivid` needs the legacy Pi camera stack, and the ground station expects JavaFX to be available in the JDK.
- **Joystick support is incomplete.** `analog_reading.ino` reads only the throttle potentiometer, and nothing forwards its serial output to the app's UDP port 8000 yet.
