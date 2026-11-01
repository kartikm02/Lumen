#!/usr/bin/env python3
"""Create local-only database credentials without overwriting an existing file."""
import os
import secrets
from pathlib import Path

path = Path('.env')
try:
    fd = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
except FileExistsError:
    raise SystemExit('.env already exists; leaving it unchanged')
with os.fdopen(fd, 'w') as stream:
    stream.write(f'LUMEN_DB_OWNER_PASSWORD={secrets.token_hex(32)}\n')
    stream.write(f'LUMEN_DB_APP_PASSWORD={secrets.token_hex(32)}\n')
print('Created .env with restricted permissions. Do not commit or share it.')
