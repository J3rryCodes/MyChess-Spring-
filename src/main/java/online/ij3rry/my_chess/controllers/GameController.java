package online.ij3rry.my_chess.controllers;

import online.ij3rry.my_chess.dao.MovementDAO;
import online.ij3rry.my_chess.dao.RoomDAO;
import online.ij3rry.my_chess.dto.SelectionDTO;
import online.ij3rry.my_chess.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/game")
public class GameController {

    @Autowired
    private GameService gameService;


    @GetMapping(value = "/room/{roomId}")
    private Flux<RoomDAO> getRoomByRoomId(@PathVariable UUID roomId){
        return gameService.getGameRoomById(roomId);
    }

    @PostMapping(value = "/play/select")
    private Mono<Boolean> selectPiece(@RequestBody SelectionDTO selectionDTO){
        return  gameService.selectPosition(selectionDTO);
    }

    @GetMapping(value = "/movements/{boardId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<MovementDAO> getMovements(@PathVariable UUID boardId){
        return gameService.getMovements(boardId);
    }
}
