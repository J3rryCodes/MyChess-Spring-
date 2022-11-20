package online.ij3rry.my_chess.services.impl;

import lombok.extern.slf4j.Slf4j;
import online.ij3rry.my_chess.dao.PlayerDAO;
import online.ij3rry.my_chess.dao.RoomDAO;
import online.ij3rry.my_chess.repositories.PlayerRepository;
import online.ij3rry.my_chess.repositories.RoomRepository;
import online.ij3rry.my_chess.services.BoardService;
import online.ij3rry.my_chess.services.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class LobbyServiceImpl implements LobbyService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Mono joinTheBobby(UUID playerId) {
        log.info("player id {} trying to join",playerId);
        return playerRepository.findById(playerId)
                .flatMap(playerDAO -> roomRepository.findTopByOrderByCreatedDesc().next().flatMap(roomDAO -> {
                    if(roomDAO.getPlayerOne().getId().equals(playerId)){
                        return Mono.just("Player already in the lobby");
                    }
                    else if (roomDAO.getPlayerTwo() == null) {
                        return updateRoom(playerDAO, roomDAO);
                    } else {
                        return createNewRoom(playerDAO);
                    }
                }).switchIfEmpty(createNewRoom(playerDAO))).switchIfEmpty(Mono.just("Player "+playerId+" not found!"));
    }

    private Mono updateRoom(PlayerDAO playerDAO, RoomDAO roomDAO) {
        log.info("Adding player id : {} to room id : {}", playerDAO.getId(), roomDAO.getId());
        roomDAO.setPlayerTwo(playerDAO);
        return roomRepository.save(roomDAO)
                .flatMap(e->boardService.initializeBoard(e));
    }

    private Mono createNewRoom(PlayerDAO playerDAO) {
        log.info("Creating new room with player id : {}", playerDAO.getId());
        return roomRepository.save(new RoomDAO(UUID.randomUUID(), "", playerDAO, null, LocalDateTime.now()));
    }
}
