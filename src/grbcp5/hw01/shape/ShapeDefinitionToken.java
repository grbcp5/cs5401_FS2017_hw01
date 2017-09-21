package grbcp5.hw01.shape;

import grbcp5.hw01.input.InputDirection;

public class ShapeDefinitionToken {
  private InputDirection m_direction;
  private int m_magnitude;

  public ShapeDefinitionToken( InputDirection m_direction, int m_magnitude ) {
    this.m_direction = m_direction;
    this.m_magnitude = m_magnitude;
  }

  public ShapeDefinitionToken( String token ) throws InvalidDirection, InvalidMagnitude {
    char directionChar = token.charAt( 0 );
    char magnitudeChar = token.charAt( 1 );

    switch ( directionChar ) {
      case 'U':
        m_direction = InputDirection.UP;
        break;
      case 'D':
        m_direction = InputDirection.DOWN;
        break;
      case 'L':
        m_direction = InputDirection.LEFT;
        break;
      case 'R':
        m_direction = InputDirection.RIGHT;
        break;
      default:
        throw new InvalidDirection( "Cannot read direction character '" + directionChar + "'" );
    }

    this.m_magnitude = magnitudeChar - '0';
    if ( this.m_magnitude <= 0 ) {
      throw new InvalidMagnitude( "Magnitude '" + this.m_magnitude + "' parsed." );
    }

  }

  public InputDirection getDirection() {
    return m_direction;
  }

  public int getMagnitude() {
    return m_magnitude;
  }

  @Override
  public String toString() {
    String result = "";

    switch ( m_direction ) {
      case UP:
        result += "^";
        break;
      case DOWN:
        result += "v";
        break;
      case LEFT:
        result += "<";
        break;
      case RIGHT:
        result += ">";
        break;
    }

    result += m_magnitude;

    return result;
  }

}
