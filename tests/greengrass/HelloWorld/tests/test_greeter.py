import pytest
import src.greeter as greeter

def test_greeting_world():
    greeting = greeter.get_greeting('World')
    assert "Hello World!" == greeting