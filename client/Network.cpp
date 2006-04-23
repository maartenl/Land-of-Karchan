/*-------------------------------------------------------------------------
svninfo: $Id: Database.java 1091 2006-03-08 22:05:19Z maartenl $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/
#include "Network.h"
#include <winsock2.h>

namespace Mmud {
//-------------------------------------------------------------------------------------
Network::Network(void)
{
    LogManager *logger = Ogre::LogManager::getSingletonPtr();
    // Initialize Winsock.
    WSADATA wsaData;
    int iResult = NO_ERROR;//WSAStartup( MAKEWORD(2,2), &wsaData );
    if ( iResult != NO_ERROR )
    {
        logger->logMessage("Error at WSAStartup()\n");
    }
}

Network::~Network(void)
{
    //WSACleanup();
}

void Network::open(void)//const String& anIp, const int aPort)
{
    /*LogManager *logger = Ogre::LogManager::getSingletonPtr();
    // Create a socket.
    m_socket = socket( AF_INET, SOCK_STREAM, IPPROTO_TCP );

    if ( m_socket == INVALID_SOCKET ) {
        logger->logMessage("Error at socket(): ");
        printf( "Error at socket(): %ld\n", WSAGetLastError() );
        WSACleanup();
        return;
    }

    // Connect to a server.
    sockaddr_in clientService;

    clientService.sin_family = AF_INET;
    clientService.sin_addr.s_addr = inet_addr( "10.0.10" );
    clientService.sin_port = htons( 3340 );

    if ( connect( m_socket, (SOCKADDR*) &clientService, sizeof(clientService) ) == SOCKET_ERROR)
    {
        logger->logMessage("Failed to connect.\n");
        WSACleanup();
        return;
    }*/
}

void Network::close(void)//const String& anIp, const int aPort)
{
    LogManager *logger = Ogre::LogManager::getSingletonPtr();
    // Closing a socket.
    if (m_socket != NULL)
    {
        //closesocket(m_socket);
        m_socket = NULL;
    }
    logger->logMessage("Socket closed.\n");
}

void Network::send(void)//const String& aMessage)
{
    // Send and receive data.
    int bytesSent;

    char sendbuf[32] = "Client: Sending data.";


    /*bytesSent = send( m_socket, sendbuf, strlen(sendbuf), 0 );
    printf( "Bytes Sent: %ld\n", bytesSent );*/
    LogManager *logger = Ogre::LogManager::getSingletonPtr();
    logger->logMessage("Bytes Sent: ");
}

void Network::receive()
{
    LogManager *logger = Ogre::LogManager::getSingletonPtr();
    int bytesRecv = SOCKET_ERROR;
    char recvbuf[32] = "";
    if ( bytesRecv == 0 ||
        (bytesRecv == SOCKET_ERROR ))//&&
        // WSAGetLastError()== WSAECONNRESET ))
    {
        // bytesRecv = recv( m_socket, recvbuf, 32, 0 );
        if ( bytesRecv == -1 )
        {
            logger->logMessage("Connection Closed.\n");
            m_socket = NULL;
            return;
        }
        if (bytesRecv < 0)
            return;
        logger->logMessage("Bytes Recv: ");
        printf( "Bytes Recv: %ld\n", bytesRecv );
    }
}
} // end of namespace